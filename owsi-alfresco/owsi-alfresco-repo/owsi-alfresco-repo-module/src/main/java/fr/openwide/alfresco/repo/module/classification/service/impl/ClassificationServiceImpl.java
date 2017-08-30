package fr.openwide.alfresco.repo.module.classification.service.impl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies.OnAddAspectPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnUpdatePropertiesPolicy;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.DuplicateChildNodeNameException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import fr.openwide.alfresco.api.core.node.exception.NoSuchNodeRemoteException;
import fr.openwide.alfresco.api.core.node.model.ChildAssociationReference;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.api.module.model.OwsiModel;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.component.model.node.model.TypeModel;
import fr.openwide.alfresco.component.model.repository.model.AppModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.component.model.search.model.restriction.RestrictionBuilder;
import fr.openwide.alfresco.repo.core.node.model.PreNodeCreationCallback;
import fr.openwide.alfresco.repo.core.node.service.NodeRepositoryService;
import fr.openwide.alfresco.repo.dictionary.node.service.NodeModelRepositoryService;
import fr.openwide.alfresco.repo.dictionary.policy.service.PolicyRepositoryService;
import fr.openwide.alfresco.repo.dictionary.search.model.BatchSearchQueryBuilder;
import fr.openwide.alfresco.repo.dictionary.search.service.NodeSearchModelRepositoryService;
import fr.openwide.alfresco.repo.module.classification.model.ClassificationBuilder;
import fr.openwide.alfresco.repo.module.classification.model.ClassificationEvent;
import fr.openwide.alfresco.repo.module.classification.model.ClassificationMode;
import fr.openwide.alfresco.repo.module.classification.model.ClassificationPolicy;
import fr.openwide.alfresco.repo.module.classification.service.ClassificationService;
import fr.openwide.alfresco.repo.module.classification.util.ClassificationCache;
import fr.openwide.alfresco.repo.remote.conversion.service.ConversionService;

public class ClassificationServiceImpl implements ClassificationService, InitializingBean, 
		OnAddAspectPolicy, OnUpdatePropertiesPolicy, 
		PreNodeCreationCallback {
	
	private static final String CLASSIFIED_NODE_TRANSACTION_KEY = ClassificationServiceImpl.class +  ".classifiedNodes";
	private static final Set<QName> IGNORED_PROPERTIES = new HashSet<>(Arrays.asList(
			ContentModel.PROP_CONTENT,
			ContentModel.PROP_CASCADE_CRC,
			ContentModel.PROP_CASCADE_TX
		));
	
	private final Logger logger = LoggerFactory.getLogger(ClassificationServiceImpl.class);
	
	private NodeModelRepositoryService nodeModelService;
	private NodeRepositoryService nodeRepositoryService;
	private NodeSearchModelRepositoryService nodeSearchModelService;
	private PolicyRepositoryService policyRepositoryService;
	
	private ConversionService conversionService;
	private TransactionService transactionService;
	private DictionaryService dictionaryService;

	private Map<NameReference, ClassificationPolicy<?>> policies = new LinkedHashMap<>();
	private Map<NameReference, ContainerModel> models = new ConcurrentHashMap<>();

	private ClassificationCache queryCache;
	private ClassificationCache pathCache;
	private ClassificationCache subFolderCache;

	@Override
	public void afterPropertiesSet() throws Exception {
		policyRepositoryService.onAddAspect(OwsiModel.classifiable, NotificationFrequency.TRANSACTION_COMMIT, this);
		policyRepositoryService.onUpdateProperties(OwsiModel.classifiable, NotificationFrequency.TRANSACTION_COMMIT, this);
		
		nodeRepositoryService.addPreNodeCreationCallback(this);
	}

	@Override
	public <T extends ContainerModel> void addClassification(T model, ClassificationPolicy<T> policy) {
		if (logger.isDebugEnabled()) {
			logger.debug("Add classification for {}.", model.getNameReference());
		}
		ClassificationPolicy<?> old = policies.put(model.getNameReference(), policy);
		if (old != null) {
			throw new IllegalStateException("There is at least two policies for " + model.getNameReference());
		}
		models.put(model.getNameReference(), model);
	}

	@Override
	public int reclassifyAll(Integer batchSize) {
		int total = 0;
		for (ContainerModel model : models.values()) {
			total += reclassify(model, batchSize);
		}
		return total;
	}
	@Override
	public int reclassify(NameReference modelName, Integer batchSize) {
		ContainerModel model = models.get(modelName);
		if (model == null) {
			throw new IllegalStateException("Unknown " + modelName);
		}
		return reclassify(model, batchSize);
	}
	
	@Override
	public int reclassify(ContainerModel model, Integer batchSize) {
		clearCaches();
		
		RestrictionBuilder restriction = new RestrictionBuilder();
		if (model instanceof TypeModel) {
			restriction.isType((TypeModel) model);
		} else {
			restriction.hasAspect((AspectModel) model);
		}
		
		logger.info("Begin reclassify of " + model);

		BatchSearchQueryBuilder searchQueryBuilder = new BatchSearchQueryBuilder();
		searchQueryBuilder.restriction(restriction);
		int nbTotal = nodeSearchModelService.searchBatch(searchQueryBuilder
				.configurationName("reclassify", "reclassify." + model)
				.frameSize(batchSize)
				.transactionSize(batchSize)
				.consumer(new Consumer<NodeReference>() {
			@Override
			public void accept(NodeReference nodeReference) {
				classify(nodeReference, ClassificationMode.RECLASSIFY);
			}
		}));
		logger.info("End reclassify of " + model + " (" + nbTotal + ")");
		return nbTotal;
	}

	@Override
	public void onAddAspect(NodeRef nodeRef, QName aspectTypeQName) {
		classify(nodeRef, ClassificationMode.CREATE);
	}
	@Override
	public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		// Appeler à la création. Sera gérer par onAddAspect()
		if (! before.isEmpty()) {
			Set<QName> newFields = new TreeSet<>(after.keySet());
			Set<QName> removedFields = new TreeSet<>();
			Set<QName> changedFields = new TreeSet<>();
			
			for (Entry<QName, Serializable> entry : before.entrySet()) {
				QName property = entry.getKey();
				Serializable oldValue = entry.getValue();
				Serializable newValue = after.get(property);
				
				newFields.remove(property);
				if ((oldValue != null || newValue != null) && !oldValue.equals(newValue)) {
					if (! IGNORED_PROPERTIES.contains(property)) {
						changedFields.add(property);
					}
				} else if (oldValue != null && newValue == null) {
					removedFields.add(property);
				}
			}
			
			if (! newFields.isEmpty() || ! removedFields.isEmpty() || ! changedFields.isEmpty()) {
				if (logger.isDebugEnabled()) {
					logger.debug("Reclassify node on new {}, removed {}, changed {}", newFields, removedFields, changedFields);
				}
				classify(nodeRef, ClassificationMode.UPDATE);
			}
		}
	}
	
	/** 
	 * Si un service demande à créer un noeud sans spécifier de parent et qu'une politique de classement est défini
	 * pour son type, on le copie dans le home folder puis on le classe.
	 */
	@Override
	public void onPreNodeCreationCallback(RepositoryNode node) {
		ChildAssociationReference primaryParent = node.getPrimaryParentAssociation();
		if (primaryParent == null || primaryParent.getParentNode() == null || primaryParent.getParentNode().getNodeReference() == null) {
			if (isClassifiable(node)) {
				Optional<NodeReference> homeFolder = getHomeFolder();
				if (homeFolder.isPresent()) {
					if (logger.isDebugEnabled()) {
						logger.debug("Node without parent is assigned to current user {} home folder. "
								+ "Will then be moved then to the classify folder.", AuthenticationUtil.getRunAsUser());
					}
					node.setPrimaryParentAssociation(new ChildAssociationReference(
							new RepositoryNode(homeFolder.get()), 
							CmModel.folder.contains.getNameReference()));
				} else {
					logger.error("Node without parent has aspect {}, but user {} does not have a home folder.", 
							OwsiModel.classifiable, AuthenticationUtil.getRunAsUser());
				}
			}
		}
	}
	
	private boolean isClassifiable(RepositoryNode node) {
		TypeDefinition type = dictionaryService.getType(conversionService.getRequired(node.getType()));
		QName classifiable = conversionService.getRequired(OwsiModel.classifiable.getNameReference());

		for (AspectDefinition defaultAspect : type.getDefaultAspects(true)) {
			if (classifiable.equals(defaultAspect.getName())) {
				return true;
			}
		}
		for (NameReference aspectName : node.getAspects()) {
			if (OwsiModel.classifiable.getNameReference().equals(aspectName)) {
				return true;
			}
			AspectDefinition aspect = dictionaryService.getAspect(conversionService.getRequired(aspectName));
			for (AspectDefinition defaultAspect : aspect.getDefaultAspects(true)) {
				if (classifiable.equals(defaultAspect.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	public Optional<NodeReference> getHomeFolder() {
		return nodeModelService.getUserHome();
	}
	public NodeReference getCompanyHome() {
		return nodeModelService.getCompanyHome();
	}
	
	private void classify(NodeRef nodeRef, ClassificationMode mode) {
		classify(conversionService.get(nodeRef), mode);
	}

	private void classify(NodeReference nodeReference, ClassificationMode mode) {
		Set<NodeReference> classifiedNodes = getClassifiedNodes();
		if (! classifiedNodes.add(nodeReference)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Node {} already classified.", nodeReference);
			}
			return;
		}
		
		NameReference type = getPolicy(nodeReference);
		if (type == null) {
			throw new IllegalStateException("Can't find a policy to classify " + nodeReference);
		}

		@SuppressWarnings("unchecked")
		ClassificationPolicy<ContainerModel> policy = (ClassificationPolicy<ContainerModel>) policies.get(type);
		ContainerModel model = models.get(type);
		
		if (logger.isDebugEnabled()) {
			logger.debug("Begin classification of node {} with policy for {}.", nodeReference, model.getNameReference());
		}
		
		NodeScopeBuilder nodeScopeBuilder = new NodeScopeBuilder()
				.nodeReference()
				.properties().set(CmModel.object)
				.properties().set(model);
		nodeScopeBuilder.assocs().primaryParent().nodeReference();
		
		policy.initNodeScopeBuilder(nodeScopeBuilder);
			
		BusinessNode node;
		try {
			node = nodeModelService.get(nodeReference, nodeScopeBuilder);
		} catch (NoSuchNodeRemoteException ex) {
			logger.warn("Node {} no longer exists. Ignoring.", nodeReference);
			return;
		}

		ClassificationBuilder builder = new ClassificationBuilder(this, node);
		ClassificationEvent event = new ClassificationEvent(node, mode, model);
		try {
			policy.classify(builder, model, event);
		} catch (RuntimeException ex) {
			// On log en debug uniquement, car cela peut être une erreur retryable, qui provoque en relance de la transaction.
			// Dans ce cas, on ne veut pas avoir l'information en erreur, puisque l'appelant ne verra rien.
			// C'est à l'appelant de tracer l'exception.
			if (logger.isDebugEnabled()) {
				logger.debug("Error during classify of " + nodeReference + " of type " + type, ex);
			}
			throw new IllegalStateException("Error during classify of " + nodeReference + " of type " + type, ex);
		}
	}
	
	private Set<NodeReference> getClassifiedNodes() {
		Set<NodeReference> nodes = AlfrescoTransactionSupport.getResource(CLASSIFIED_NODE_TRANSACTION_KEY);
		if (nodes == null) {
			nodes = new HashSet<>();
			AlfrescoTransactionSupport.bindResource(CLASSIFIED_NODE_TRANSACTION_KEY, nodes);
		}
		return nodes;
	}
	
	private String getPath(NodeReference nodeReference) {
		return nodeModelService.get(nodeReference, new NodeScopeBuilder().path()).getPath();
	}

	public String getUniqueName(NodeReference folder, String originalName) {
		String extension = FilenameUtils.getExtension(originalName);
		if (! extension.isEmpty()) {
			extension = "." + extension;
		}
		String baseName = FilenameUtils.removeExtension(originalName);
		String name = originalName;
		int index = 1;
		while (nodeModelService.getChildByName(folder, name).isPresent()) {
			name = baseName + "-" + (index ++) + extension;
		}
		return name;
	}
	public void setNewName(NodeReference node, String newName) {
		nodeModelService.setProperty(node, CmModel.object.name, newName);
	}
	
	public void moveNode(NodeReference node, NodeReference destinationFolder) {
		if (logger.isDebugEnabled()) {
			logger.debug("Move node {} to {} : {}.", node.getReference(), destinationFolder, getPath(destinationFolder));
		}
		nodeModelService.moveNode(node, destinationFolder);
	}
	public NodeReference copyNode(NodeReference node, NodeReference destinationFolder, Optional<String> newName) {
		if (logger.isDebugEnabled()) {
			logger.debug("Copy node {} to {} : {}.", node.getReference(), destinationFolder, getPath(destinationFolder));
		}
		NodeReference copyNodeReference = nodeModelService.copy(node, destinationFolder, newName);
		getClassifiedNodes().add(copyNodeReference);
		return copyNodeReference;
	}
	
	public void createFileLink(NodeReference nodeReference, NodeReference destinationFolder, Optional<String> linkNameOpt) {
		String linkName = linkNameOpt.isPresent() ? linkNameOpt.get() 
				: "Link to " + nodeModelService.getProperty(nodeReference, CmModel.object.name);
		nodeModelService.create(new BusinessNode(destinationFolder, AppModel.fileLink, linkName)
				.properties().set(AppModel.fileLink.destination, nodeReference));
	}
	
	public void createSecondaryParent(NodeReference node, NodeReference destinationFolder) {
		if (logger.isDebugEnabled()) {
			logger.debug("Add link from node {} to {} : {}.", node.getReference(), destinationFolder, getPath(destinationFolder));
		}
		nodeModelService.addChild(destinationFolder, node);
	}
	public void deleteSecondaryParents(NodeReference nodeReference, ChildAssociationModel childAssociationModel) {
		nodeModelService.unlinkSecondaryParents(nodeReference, childAssociationModel);
	}
	
	private NameReference getPolicy(NodeReference nodeReference) {
		NameReference result = null;

		NameReference type = nodeModelService.getType(nodeReference);
		QName typeQName = conversionService.getRequired(type);
		
		// Cherche parmi les super types
		ClassDefinition superType = dictionaryService.getType(typeQName);
		while (superType != null) {
			ClassificationPolicy<?> policy = policies.get(conversionService.get(superType.getName()));
			if (policy != null) {
				result = setResult(nodeReference, result, conversionService.get(superType.getName()));
			}
			superType = superType.getParentClassDefinition();
		}
		
		// Cherche parmi les aspects
		for (NameReference aspect : policies.keySet()) {
			if (nodeModelService.hasAspect(nodeReference, aspect)) {
				result = setResult(nodeReference, result, aspect);
			}
		}
		return result;
	}
	
	private NameReference setResult(NodeReference nodeReference, NameReference previousResult, NameReference newResult) {
		if (previousResult != null) {
			logger.warn("Ambigious classification policies: Node {} match for {} and {}. Using first one.", nodeReference, previousResult, newResult);
			return previousResult;
		}
		return newResult;
	}

	public NodeReference subFolder(String folderName, Supplier<BusinessNode> folderNodeSupplier, NodeReference destinationFolder) {
		ChildAssociationModel associationType = CmModel.folder.contains;

		String cacheKey = destinationFolder + "/" + associationType + "/" + folderName;
		return subFolderCache.get(nodeModelService, cacheKey, 
				() -> nodeModelService.getChildByName(destinationFolder, folderName, associationType),
				() -> {
			BusinessNode folderNode = folderNodeSupplier.get();
			folderNode.properties().name(folderName);
			if (folderNode.getRepositoryNode().getType() == null) {
				folderNode.getRepositoryNode().setType(CmModel.folder.getNameReference());
			}
			folderNode.aspect(OwsiModel.deleteIfEmpty);
			folderNode.assocs().primaryParent(associationType).nodeReference(destinationFolder);
			
			try {
				// Execute dans une sous transaction. Sinon, une éventuelle DuplicateChildNodeNameException rollback la transaction en cours.
				return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<NodeReference>() {
					@Override
					public NodeReference execute() {
						return nodeModelService.create(folderNode);
					}
				}, false, true);
			} catch (DuplicateChildNodeNameException ex) {
				// si un autre processus a crée le même répertoire entre temps, on recommence le fait de le chercher
				return nodeModelService.getChildByName(destinationFolder, folderName, associationType).get();
			}
		});
	} 
	
	@Override
	public void clearCaches() {
		queryCache.clear();
		pathCache.clear();
		subFolderCache.clear();
	}
	
	public Optional<NodeReference> searchUniqueReference(RestrictionBuilder restrictionBuilder) {
		return nodeSearchModelService.searchUniqueReference(restrictionBuilder);
	}

	public Optional<NodeReference> searchUniqueReferenceCached(RestrictionBuilder restrictionBuilder) {
		String cacheKey = restrictionBuilder.toFtsQuery();
		return queryCache.get(nodeModelService, cacheKey, 
				() -> searchUniqueReference(restrictionBuilder));
	}

	public Optional<NodeReference> getByNamedPath(String ... names) {
		return nodeModelService.getByNamedPath(names);
	}
	public Optional<NodeReference> getByNamedPathCached(String ... names) {
		String cacheKey = Arrays.toString(names);
		return pathCache.get(nodeModelService, cacheKey, 
				() -> getByNamedPath(names));
	}
	
	public void setNodeModelService(NodeModelRepositoryService nodeModelService) {
		this.nodeModelService = nodeModelService; 
	}
	public void setNodeRepositoryService(NodeRepositoryService nodeRepositoryService) {
		this.nodeRepositoryService = nodeRepositoryService;
	}
	public void setNodeSearchModelService(NodeSearchModelRepositoryService nodeSearchModelService) {
		this.nodeSearchModelService = nodeSearchModelService;
	}
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}
	public void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}
	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}
	public void setPolicyRepositoryService(PolicyRepositoryService policyRepositoryService) {
		this.policyRepositoryService = policyRepositoryService;
	}
	
	public void setPathCacheMaxSize(int maxSize) {
		this.pathCache = new ClassificationCache(maxSize);
	}
	public void setQueryCacheMaxSize(int maxSize) {
		this.queryCache = new ClassificationCache(maxSize);
	}
	public void setSubFolderCacheMaxSize(int maxSize) {
		this.subFolderCache = new ClassificationCache(maxSize);
	}

	public void deletePrevious(NodeReference destinationFolder, String childName) {
		Optional<NodeReference> child = nodeModelService.getChildByName(destinationFolder, childName);
		if (child.isPresent()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Delete previous node {} in {}", childName, destinationFolder);
			}
			nodeModelService.delete(child.get());
		}
	}

}
