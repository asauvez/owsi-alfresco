package fr.openwide.alfresco.repo.module.classification.service.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import fr.openwide.alfresco.api.core.node.exception.DuplicateChildNodeNameRemoteException;
import fr.openwide.alfresco.api.core.node.model.ChildAssociationReference;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.module.model.OwsiModel;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.component.model.node.model.TypeModel;
import fr.openwide.alfresco.component.model.node.service.NodeModelService;
import fr.openwide.alfresco.component.model.repository.model.AppModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.component.model.repository.model.StModel;
import fr.openwide.alfresco.component.model.search.model.restriction.Restriction;
import fr.openwide.alfresco.component.model.search.model.restriction.RestrictionBuilder;
import fr.openwide.alfresco.repo.core.node.model.PreNodeCreationCallback;
import fr.openwide.alfresco.repo.core.node.service.NodeRepositoryService;
import fr.openwide.alfresco.repo.dictionary.node.service.NodeModelRepositoryService;
import fr.openwide.alfresco.repo.dictionary.policy.service.PolicyRepositoryService;
import fr.openwide.alfresco.repo.dictionary.search.model.BatchSearchQueryBuilder;
import fr.openwide.alfresco.repo.dictionary.search.service.NodeSearchModelRepositoryService;
import fr.openwide.alfresco.repo.module.classification.model.ClassificationEvent;
import fr.openwide.alfresco.repo.module.classification.model.ClassificationMode;
import fr.openwide.alfresco.repo.module.classification.model.builder.ClassificationBuilder;
import fr.openwide.alfresco.repo.module.classification.model.builder.UniqueNameGenerator;
import fr.openwide.alfresco.repo.module.classification.model.policy.ClassificationPolicy;
import fr.openwide.alfresco.repo.module.classification.model.policy.ConsumerClassificationPolicy;
import fr.openwide.alfresco.repo.module.classification.model.policy.FreeMarkerClassificationPolicy;
import fr.openwide.alfresco.repo.module.classification.service.ClassificationService;
import fr.openwide.alfresco.repo.module.classification.util.ClassificationCache;
import fr.openwide.alfresco.repo.remote.conversion.service.ConversionService;

public class ClassificationServiceImpl implements ClassificationService, InitializingBean, 
		OnAddAspectPolicy, OnUpdatePropertiesPolicy, 
		PreNodeCreationCallback, 
		ApplicationListener<ContextRefreshedEvent> {
	
	private static final String CLASSIFIED_NODE_TRANSACTION_KEY = ClassificationServiceImpl.class +  ".classifiedNodes";
	private static final Set<QName> IGNORED_PROPERTIES = new HashSet<>(Arrays.asList(
			ContentModel.PROP_CONTENT,
			ContentModel.PROP_CASCADE_CRC,
			ContentModel.PROP_CASCADE_TX
		));
	
	private final Logger logger = LoggerFactory.getLogger(ClassificationServiceImpl.class);
	
	@Autowired @Qualifier("global-properties")
	private Properties globalProperties;
	
	@Autowired private NodeModelService nodeModelService;
	@Autowired private NodeModelRepositoryService nodeModelRepositoryService;
	@Autowired private NodeRepositoryService nodeRepositoryService;
	private NodeSearchModelRepositoryService nodeSearchModelService;
	private PolicyRepositoryService policyRepositoryService;
	@Autowired private FileFolderService fileFolderService;
	
	private ConversionService conversionService;
	private TransactionService transactionService;
	private DictionaryService dictionaryService;

	private Map<NameReference, ClassificationPolicy<?>> policies = new LinkedHashMap<>();
	private Map<NameReference, ContainerModel> models = new ConcurrentHashMap<>();

	private ClassificationCache queryCache;
	private ClassificationCache pathCache;
	private ClassificationCache subFolderCache;
	
	private boolean addDeleteIfEmptyAspect;
	private boolean addClassificationDate;
	private boolean createSubFolderInInnerTransaction;

	@Override
	public void afterPropertiesSet() throws Exception {
		policyRepositoryService.onAddAspect(OwsiModel.classifiable, NotificationFrequency.TRANSACTION_COMMIT, this);
		policyRepositoryService.onUpdateProperties(OwsiModel.classifiable, NotificationFrequency.TRANSACTION_COMMIT, this);
		
		nodeRepositoryService.addPreNodeCreationCallback(this);
	}
	
	/** On fait dans ContextRefreshedEvent car les models peuvent ne pas avoir été initialisé */
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		for (String nameReference : globalProperties.getProperty("owsi.classification.freemarker.models", "").split(",")) {
			if (! nameReference.trim().isEmpty()) {
				ContainerModel containerModel = new ContainerModel(NameReference.create(nameReference.trim()));
				try {
					addClassification(containerModel, new FreeMarkerClassificationPolicy(globalProperties, containerModel));
				} catch (IOException e) {
					logger.error(nameReference, e);
					throw new IllegalStateException(nameReference, e);
				}
				policyRepositoryService.onAddAspect(containerModel, NotificationFrequency.TRANSACTION_COMMIT, this);
			}
		}
	}
	
	@Override
	public void autoClassification(ContainerModel containerModel) {
		policyRepositoryService.onAddAspect(containerModel, NotificationFrequency.TRANSACTION_COMMIT, this);
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
	public <T extends ContainerModel> void addClassification(T model, Consumer<ClassificationBuilder> consumer) {
		addClassification(model, new ConsumerClassificationPolicy<T>(consumer));
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
	public int reclassify(ContainerModel model, Integer batchSize, Restriction ...restrictions) {
		clearCaches();
		
		RestrictionBuilder restriction = new RestrictionBuilder();
		if (model instanceof TypeModel) {
			restriction.isType((TypeModel) model);
		} else {
			restriction.hasAspect((AspectModel) model);
		}
		for (Restriction curRestriction : restrictions) {
			restriction.and(curRestriction);
		}
		
		logger.info("Begin reclassify of " + model);

		BatchSearchQueryBuilder searchQueryBuilder = new BatchSearchQueryBuilder();
		searchQueryBuilder.restriction(restriction);
		int nbTotal = nodeSearchModelService.searchBatch(searchQueryBuilder
				.configurationName("reclassify", "reclassify." + model)
				.frameSize(batchSize)
				.transactionSize(batchSize)
				.consumer(new Consumer<NodeRef>() {
			@Override
			public void accept(NodeRef nodeRef) {
				classify(nodeRef, ClassificationMode.RECLASSIFY);
			}
		}));
		logger.info("End reclassify of " + model + " (" + nbTotal + ")");
		return nbTotal;
	}

	@Override
	public void onAddAspect(NodeRef nodeRef, QName aspectTypeQName) {
		if (! nodeModelRepositoryService.exists(nodeRef)) {
			logger.debug("onAddAspect() sur une node qui n'existe plus " + nodeRef);
			return;
		}
		
		if (OwsiModel.classifiable.getNameReference().equals(conversionService.get(aspectTypeQName))) {
			classify(nodeRef, ClassificationMode.CREATE);
		} else {
			nodeModelRepositoryService.addAspect(nodeRef, OwsiModel.classifiable);
		}
	}
	@Override
	public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		if (! nodeModelRepositoryService.exists(nodeRef)) {
			logger.debug("onUpdateProperties() sur une node qui n'existe plus " + nodeRef);
			return;
		}
		
		// Appeler à la création. Sera gérer par onAddAspect()
		if (! before.isEmpty()) {
			Set<QName> newFields = new TreeSet<>(after.keySet());
			newFields.removeAll(before.keySet());
			
			Set<QName> removedFields = new TreeSet<>(before.keySet());
			removedFields.removeAll(after.keySet());
			
			Set<QName> changedFields = new TreeSet<>();
			for (Entry<QName, Serializable> entry : before.entrySet()) {
				QName property = entry.getKey();
				Serializable oldValue = entry.getValue();
				Serializable newValue = after.get(property);
				
				if (	! newFields.contains(property) 
						&& ! IGNORED_PROPERTIES.contains(property)
						&& ! Objects.equals(oldValue, newValue)) {
						changedFields.add(property);
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
				Optional<NodeRef> homeFolder = getHomeFolder();
				if (homeFolder.isPresent()) {
					if (logger.isDebugEnabled()) {
						logger.debug("Node without parent is assigned to current user {} home folder. "
								+ "Will then be moved then to the classify folder.", AuthenticationUtil.getRunAsUser());
					}
					node.setPrimaryParentAssociation(new ChildAssociationReference(
							new RepositoryNode(conversionService.get(homeFolder.get())), 
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

	public Optional<NodeRef> getHomeFolder() {
		return nodeModelRepositoryService.getUserHome();
	}
	public NodeRef getCompanyHome() {
		return nodeModelRepositoryService.getCompanyHome();
	}
	
	@Override
	public void classify(NodeRef nodeRef) {
		classify(nodeRef, ClassificationMode.MANUAL);
	}
	
	private void classify(NodeRef nodeRef, ClassificationMode mode) {
		Set<NodeRef> classifiedNodes = getClassifiedNodes();
		if (! classifiedNodes.add(nodeRef)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Node {} already classified.", nodeRef);
			}
			return;
		}
		
		NameReference type = getPolicy(nodeRef);
		if (type == null) {
			throw new IllegalStateException("Can't find a policy to classify " + nodeRef);
		}

		@SuppressWarnings("unchecked")
		ClassificationPolicy<ContainerModel> policy = (ClassificationPolicy<ContainerModel>) policies.get(type);
		ContainerModel model = models.get(type);
		
		if (logger.isDebugEnabled()) {
			logger.debug("Begin classification of node {} with policy for {}.", nodeRef, type);
		}
		
		if (! nodeModelRepositoryService.exists(nodeRef)) {
			logger.warn("Node {} no longer exists. Ignoring.", nodeRef);
			return;
		}

		ClassificationEvent event = new ClassificationEvent(nodeRef, mode, model);
		ClassificationBuilder builder = new ClassificationBuilder(this, event);
		try {
			policyRepositoryService.disableBehaviours(Arrays.asList(
						OwsiModel.classifiable,
						CmModel.versionable,
						CmModel.auditable), 
					() -> { 
				policy.classify(builder, model, event);

				if (addClassificationDate) {
					nodeModelRepositoryService.setProperty(nodeRef, OwsiModel.classifiable.classificationDate, new Date());
				}
			});
			
		} catch (RuntimeException ex) {
			// On log en debug uniquement, car cela peut être une erreur retryable, qui provoque en relance de la transaction.
			// Dans ce cas, on ne veut pas avoir l'information en erreur, puisque l'appelant ne verra rien.
			// C'est à l'appelant de tracer l'exception.
			if (logger.isDebugEnabled()) {
				logger.debug("Error during classify of " + nodeRef + " of type " + type, ex);
			}
			throw new IllegalStateException("Error during classify of " + nodeRef + " of type " + type, ex);
		}
	}
	
	private Set<NodeRef> getClassifiedNodes() {
		Set<NodeRef> nodes = AlfrescoTransactionSupport.getResource(CLASSIFIED_NODE_TRANSACTION_KEY);
		if (nodes == null) {
			nodes = new HashSet<>();
			AlfrescoTransactionSupport.bindResource(CLASSIFIED_NODE_TRANSACTION_KEY, nodes);
		}
		return nodes;
	}
	
	private String getPath(NodeRef nodeRef) {
		return nodeModelRepositoryService.getPath(nodeRef);
	}

	public void setNewName(NodeRef node, String newName) {
		try {
			fileFolderService.rename(node, newName);
		} catch (FileExistsException | FileNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public String getUniqueName(NodeRef document, Collection<NodeRef> destinationFolders, UniqueNameGenerator uniqueNameGenerator) {
		String originalName = nodeModelRepositoryService.getProperty(document, CmModel.object.name);
		
		String newName = originalName;
		while (existsNewName(document, destinationFolders, newName)) {
			newName = uniqueNameGenerator.generateNextName(originalName);
		}
		return newName;
	}
	private boolean existsNewName(NodeRef document, Collection<NodeRef> destinationFolders, String newName) {
		for (NodeRef folder : destinationFolders) {
			Optional<NodeRef> childByName = nodeModelRepositoryService.getChildByName(folder, newName);
			if (childByName.isPresent() && ! childByName.get().equals(document)) {
				return true;
			}
		}
		return false;
	}
	
	public void setContentStore(NodeRef node, String storeName) {
		nodeModelRepositoryService.setProperty(node, CmModel.storeSelector.storeName, storeName);
	}
	public void setIndex(NodeRef node, boolean isIndexed) {
		nodeModelRepositoryService.setProperty(node, CmModel.indexControl.isIndexed, isIndexed);
	}
	public void setIndexContent(NodeRef node, boolean isContentIndexed) {
		nodeModelRepositoryService.setProperty(node, CmModel.indexControl.isContentIndexed, isContentIndexed);
	}
	
	public void moveNode(NodeRef node, NodeRef destinationFolder) {
		NodeRef actualPrimaryParent = nodeModelRepositoryService.getPrimaryParent(node).get();
		if (actualPrimaryParent.equals(destinationFolder)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Move node {} : Already in correct folder", node);
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Move node {} to {} : {}.", node, destinationFolder, getPath(destinationFolder));
			}
			nodeModelRepositoryService.moveNode(node, destinationFolder);
		}
	}
	public NodeRef copyNode(NodeRef node, NodeRef destinationFolder, Optional<String> newName) {
		if (logger.isDebugEnabled()) {
			logger.debug("Copy node {} to {} : {}.", node, destinationFolder, getPath(destinationFolder));
		}
		NodeRef copyNodeRef = nodeModelRepositoryService.copy(node, destinationFolder, newName);
		getClassifiedNodes().add(copyNodeRef);
		return copyNodeRef;
	}
	
	public void createFileLink(NodeRef nodeRef, NodeRef destinationFolder, Optional<String> linkNameOpt) {
		String linkName = linkNameOpt.isPresent() ? linkNameOpt.get() 
				: "Link to " + nodeModelRepositoryService.getProperty(nodeRef, CmModel.object.name);
		nodeModelService.create(new BusinessNode(conversionService.get(destinationFolder), AppModel.fileLink, linkName)
				.properties().set(AppModel.fileLink.destination, conversionService.get(nodeRef)));
	}
	
	public void createSecondaryParent(NodeRef node, NodeRef destinationFolder) {
		if (logger.isDebugEnabled()) {
			logger.debug("Add link from node {} to {} : {}.", node, destinationFolder, getPath(destinationFolder));
		}
		nodeModelRepositoryService.addChild(destinationFolder, node);
	}
	public void deleteSecondaryParents(NodeRef nodeRef, ChildAssociationModel childAssociationModel) {
		nodeModelRepositoryService.unlinkSecondaryParents(nodeRef, childAssociationModel);
	}
	
	private NameReference getPolicy(NodeRef nodeRef) {
		NameReference result = nodeModelRepositoryService.getProperty(nodeRef, OwsiModel.classifiable.classificationPolicy);

		NameReference type = nodeModelRepositoryService.getType(nodeRef);
		QName typeQName = conversionService.getRequired(type);
		
		// Cherche parmi les super types
		ClassDefinition superType = dictionaryService.getType(typeQName);
		while (superType != null) {
			ClassificationPolicy<?> policy = policies.get(conversionService.get(superType.getName()));
			if (policy != null) {
				result = setResult(nodeRef, result, conversionService.get(superType.getName()));
			}
			superType = superType.getParentClassDefinition();
		}
		
		// Cherche parmi les aspects
		for (NameReference aspect : policies.keySet()) {
			if (nodeModelRepositoryService.hasAspect(nodeRef, aspect)) {
				result = setResult(nodeRef, result, aspect);
			}
		}
		return result;
	}
	
	private NameReference setResult(NodeRef nodeRef, NameReference previousResult, NameReference newResult) {
		if (previousResult != null) {
			logger.warn("Ambigious classification policies: Node {} match for {} and {}. Using first one.", nodeRef, previousResult, newResult);
			return previousResult;
		}
		return newResult;
	}

	public NodeRef subFolder(String folderName, Supplier<BusinessNode> folderNodeSupplier, NodeRef destinationFolder) {
		ChildAssociationModel associationType = CmModel.folder.contains;

		String cleanFolderName = folderName.replace('"', ' ').replace('?', ' ').replace('*', ' ')
				.replace('\\', ' ').replace('/', ' ').replace('|', ' ').replace(':', ' ')
				.replace('<', ' ').replace('>', ' ').trim(); 
		
		String cacheKey = destinationFolder + "/" + associationType + "/" + cleanFolderName;
		return subFolderCache.get(nodeModelRepositoryService, cacheKey, 
				() -> nodeModelRepositoryService.getChildByName(destinationFolder, cleanFolderName, associationType),
				() -> {
			BusinessNode folderNode = folderNodeSupplier.get();
			
			folderNode.properties().name(cleanFolderName);
			if (! cleanFolderName.equals(folderName) && folderNode.properties().get(CmModel.titled.title) == null) {
				folderNode.properties().set(CmModel.titled.title, folderName);
			}
			if (folderNode.getRepositoryNode().getType() == null) {
				folderNode.getRepositoryNode().setType(CmModel.folder.getNameReference());
			}
			
			if (addDeleteIfEmptyAspect) {
				folderNode.aspect(OwsiModel.deleteIfEmpty);
			}
			
			folderNode.assocs().primaryParent(associationType).nodeReference(conversionService.get(destinationFolder));
			
			if (logger.isDebugEnabled()) {
				logger.debug("Create subfolder {}", cleanFolderName);
			}

			if (createSubFolderInInnerTransaction) {
				try {
					// Execute dans une sous transaction. Sinon, une éventuelle DuplicateChildNodeNameException rollback la transaction en cours.
					return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<NodeRef>() {
						@Override
						public NodeRef execute() {
							return conversionService.getRequired(nodeModelService.create(folderNode));
						}
					}, false, true);
				} catch (DuplicateChildNodeNameRemoteException ex) {
					// si un autre processus a crée le même répertoire entre temps, on recommence le fait de le chercher
					return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<NodeRef>() {
						@Override
						public NodeRef execute() {
							Optional<NodeRef> childByName = nodeModelRepositoryService.getChildByName(destinationFolder, cleanFolderName, associationType);
							return childByName.get();
						}
					}, true, true);
				}
			} else {
				return conversionService.getRequired(nodeModelService.create(folderNode));
			}
		});
	} 
	
	@Override
	public void clearCaches() {
		queryCache.clear();
		pathCache.clear();
		subFolderCache.clear();
	}
	
	public List<NodeRef> searchReference(RestrictionBuilder restrictionBuilder) {
		// Pas de cache
		return nodeSearchModelService.searchReference(restrictionBuilder); 
	}
	public Optional<NodeRef> searchUniqueReference(RestrictionBuilder restrictionBuilder) {
		String cacheKey = restrictionBuilder.toFtsQuery();
		return queryCache.get(nodeModelRepositoryService, cacheKey, 
				() -> nodeSearchModelService.searchUniqueReference(restrictionBuilder)
					.map(node -> conversionService.getRequired(node)));
	}

	public NodeModelRepositoryService getNodeModelService() {
		return nodeModelRepositoryService;
	}
	
	public Optional<NodeRef> getByNamedPath(String ... names) {
		// Pas nécessaire d'avoir le droit de lecture sur les dossiers intermédiaires.
		return AuthenticationUtil.runAsSystem(() -> 
			nodeModelRepositoryService.getByNamedPath(names));
	}
	public Optional<NodeRef> getByNamedPathCached(String ... names) {
		String cacheKey = Arrays.toString(names);
		return pathCache.get(nodeModelRepositoryService, cacheKey, 
				() -> getByNamedPath(names));
	}
	public Set<String> getTagsName(NodeRef nodeRef) {
		List<NodeRef> tags = getNodeModelService().getProperty(nodeRef, CmModel.taggable.taggable); 
		if (tags == null) {
			tags = Collections.emptyList(); 
		}
		return tags.stream()
				.map(tag -> getNodeModelService().getProperty(tag, CmModel.object.name))
				.collect(Collectors.toSet());
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
	public void setAddDeleteIfEmptyAspect(boolean addDeleteIfEmptyAspect) {
		this.addDeleteIfEmptyAspect = addDeleteIfEmptyAspect;
	}
	public void setAddClassificationDate(boolean addClassificationDate) {
		this.addClassificationDate = addClassificationDate;
	}
	public void setCreateSubFolderInInnerTransaction(boolean createSubFolderInInnerTransaction) {
		this.createSubFolderInInnerTransaction = createSubFolderInInnerTransaction;
	}
	

	public void deletePrevious(NodeRef destinationFolder, String childName) {
		Optional<NodeRef> child = nodeModelRepositoryService.getChildByName(destinationFolder, childName);
		if (child.isPresent()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Delete previous node {} in {}", childName, destinationFolder);
			}
			nodeModelRepositoryService.deleteNode(child.get());
		}
	}

	public void delete(NodeRef nodeRef, boolean permanently) {
		if (permanently) {
			nodeModelRepositoryService.deleteNodePermanently(nodeRef);
		} else {
			nodeModelRepositoryService.deleteNode(nodeRef);
		}
	}

	public Optional<NodeRef> getSiteNode(NodeRef nodeRef) {
		Optional<NodeRef> parent = nodeModelRepositoryService.getPrimaryParent(nodeRef);
		if (! parent.isPresent() || nodeModelRepositoryService.isType(parent.get(), StModel.site)) {
			return parent;
		} else {
			return getSiteNode(parent.get());
		}
	}

	public void setClassificicationState(NodeRef nodeRef, String newState) {
		nodeModelRepositoryService.setProperty(nodeRef, OwsiModel.classifiable.classificationState, newState);
	}

}
