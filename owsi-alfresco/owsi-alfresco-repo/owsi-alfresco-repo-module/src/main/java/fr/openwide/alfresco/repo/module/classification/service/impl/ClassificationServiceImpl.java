package fr.openwide.alfresco.repo.module.classification.service.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies.OnAddAspectPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnUpdatePropertiesPolicy;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import fr.openwide.alfresco.api.core.node.exception.DuplicateChildNodeNameRemoteException;
import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.module.model.OwsiModel;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.component.model.node.model.TypeModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.SinglePropertyModel;
import fr.openwide.alfresco.component.model.repository.model.AppModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.component.model.repository.model.StModel;
import fr.openwide.alfresco.component.model.search.model.restriction.RestrictionBuilder;
import fr.openwide.alfresco.repo.core.configurationlogger.AlfrescoGlobalProperties;
import fr.openwide.alfresco.repo.dictionary.node.service.NodeModelRepositoryService;
import fr.openwide.alfresco.repo.dictionary.node.service.UniqueNameRepositoryService;
import fr.openwide.alfresco.repo.dictionary.node.service.impl.UniqueNameGenerator;
import fr.openwide.alfresco.repo.dictionary.permission.service.PermissionRepositoryService;
import fr.openwide.alfresco.repo.dictionary.policy.service.PolicyRepositoryService;
import fr.openwide.alfresco.repo.dictionary.search.model.BatchSearchQueryBuilder;
import fr.openwide.alfresco.repo.dictionary.search.service.NodeSearchModelRepositoryService;
import fr.openwide.alfresco.repo.module.classification.model.ClassificationEvent;
import fr.openwide.alfresco.repo.module.classification.model.ClassificationMode;
import fr.openwide.alfresco.repo.module.classification.model.ReclassifyParams;
import fr.openwide.alfresco.repo.module.classification.model.builder.AbstractClassificationBuilder;
import fr.openwide.alfresco.repo.module.classification.model.builder.ClassificationBuilder;
import fr.openwide.alfresco.repo.module.classification.model.policy.ClassificationPolicy;
import fr.openwide.alfresco.repo.module.classification.model.policy.ConsumerClassificationPolicy;
import fr.openwide.alfresco.repo.module.classification.model.policy.FreeMarkerClassificationPolicy;
import fr.openwide.alfresco.repo.module.classification.service.ClassificationService;
import fr.openwide.alfresco.repo.module.classification.util.ClassificationCache;
import fr.openwide.alfresco.repo.remote.conversion.service.ConversionService;
import fr.openwide.alfresco.repo.treeaspect.service.ChildAspectService;
import fr.openwide.alfresco.repo.treeaspect.service.RegisterRootPropertyName;
import fr.openwide.alfresco.repo.treeaspect.service.TreeAspectService;

public class ClassificationServiceImpl implements ClassificationService, InitializingBean, 
		OnAddAspectPolicy, OnUpdatePropertiesPolicy, 
		ApplicationListener<ContextRefreshedEvent> {
	
	private static final String CLASSIFIED_NODE_TRANSACTION_KEY = ClassificationServiceImpl.class +  ".classifiedNodes";
	private static final Set<QName> IGNORED_PROPERTIES = new HashSet<>(Arrays.asList(
			ContentModel.PROP_CONTENT,
			ContentModel.PROP_CASCADE_CRC,
			ContentModel.PROP_CASCADE_TX,
			ContentModel.PROP_VERSION_LABEL,
			ContentModel.PROP_VERSION_TYPE
		));
	
	private final Logger logger = LoggerFactory.getLogger(ClassificationServiceImpl.class);
	
	@Autowired private AlfrescoGlobalProperties globalProperties;
	@Autowired private NodeModelRepositoryService nodeModelRepositoryService;
	private NodeSearchModelRepositoryService nodeSearchModelService;
	private PolicyRepositoryService policyRepositoryService;
	@Autowired private PermissionRepositoryService permissionRepositoryService;
	@Autowired private UniqueNameRepositoryService uniqueNameRepositoryService;
	@Autowired private FileFolderService fileFolderService;
	@Autowired private TreeAspectService treeAspectService;
	@Autowired private ChildAspectService childAspectService;
	@Autowired private RegisterRootPropertyName registerRootPropertyName;
	
	private ConversionService conversionService;
	private TransactionService transactionService;
	private DictionaryService dictionaryService;
	@Autowired private ContentService contentService;

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
	}
	
	/** On fait dans ContextRefreshedEvent car les models peuvent ne pas avoir été initialisé */
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		for (String nameReference : globalProperties.getPropertyList("owsi.classification.freemarker.models", "")) {
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
		policyRepositoryService.onUpdateProperties(containerModel, NotificationFrequency.TRANSACTION_COMMIT, this);
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
	public int reclassify(ReclassifyParams params) {
		clearCaches();
		
		if (params.getContainer() == null) {
			int total = 0;
			for (ContainerModel model : models.values()) {
				params.container(model);
				total += reclassify(params);
			}
			params.container((NameReference) null);
			return total;
		}
		
		ContainerModel container = models.get(params.getContainer());
		if (container == null) {
			throw new IllegalStateException("Unknown " + params.getContainer());
		}
		
		RestrictionBuilder restrictions = new RestrictionBuilder();
		if (container instanceof TypeModel) {
			restrictions.isType((TypeModel) container);
		} else {
			restrictions.hasAspect((AspectModel) container);
		}
		restrictions.add(params.getRestrictions());
		
		logger.info("Begin reclassify of " + container + " : " + restrictions.toFtsQuery());

		BatchSearchQueryBuilder searchQueryBuilder = new BatchSearchQueryBuilder();
		if (params.isUseCmis()) {
			searchQueryBuilder.restrictionCmisContent(restrictions);
		} else {
			searchQueryBuilder.restriction(restrictions);
		}
		
		int nbTotal = nodeSearchModelService.searchBatch(searchQueryBuilder
				.configurationName("reclassify", "reclassify." + container)
				.frameSize(params.getBatchSize())
				.transactionSize(params.getBatchSize())
				.consumer(new Consumer<NodeRef>() {
			@Override
			public void accept(NodeRef nodeRef) {
				classify(new ClassificationEvent(nodeRef, ClassificationMode.RECLASSIFY));
			}
		}));
		logger.info("End reclassify of " + container + " (" + nbTotal + ")");
		return nbTotal;
	}

	@Override
	public void onAddAspect(NodeRef nodeRef, QName aspectTypeQName) {
		if (! nodeModelRepositoryService.exists(nodeRef)) {
			logger.debug("onAddAspect() sur une node qui n'existe plus " + nodeRef);
			return;
		}
		
		if (OwsiModel.classifiable.getNameReference().equals(conversionService.get(aspectTypeQName))) {
			classify(new ClassificationEvent(nodeRef, ClassificationMode.CREATE));
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
				classify(new ClassificationEvent(nodeRef, before, after));
			}
		}
	}
	
	public Optional<NodeRef> getHomeFolder() {
		return nodeModelRepositoryService.getUserHome();
	}
	public NodeRef getCompanyHome() {
		return nodeModelRepositoryService.getCompanyHome();
	}
	
	@Override
	public void classify(NodeRef nodeRef) {
		classify(new ClassificationEvent(nodeRef, ClassificationMode.MANUAL));
	}
	
	private void classify(ClassificationEvent event) {
		NodeRef nodeRef = event.getNodeRef();
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
		event.setModel(model);
		
		if (logger.isDebugEnabled()) {
			logger.debug("Begin classification of node {} with policy for {}.", nodeRef, type);
		}
		
		if (! nodeModelRepositoryService.exists(nodeRef)) {
			logger.warn("Node {} no longer exists. Ignoring.", nodeRef);
			return;
		}

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
			// On log en warn uniquement, car cela peut être une erreur retryable, qui provoque en relance de la transaction.
			// Dans ce cas, on ne veut pas avoir l'information en erreur, puisque l'appelant ne verra rien.
			// C'est à l'appelant de tracer l'exception en error.
			logger.warn("Error during classify of " + nodeRef + " of type " + type, ex);
			
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
		return getUniqueName(document, Optional.empty(), destinationFolders, uniqueNameGenerator);
	}
	public String getUniqueName(NodeRef document, Optional<String> newName, Collection<NodeRef> destinationFolders, 
			UniqueNameGenerator uniqueNameGenerator) {
		String expectedNewName = newName.orElse(nodeModelRepositoryService.getProperty(document, CmModel.object.name));
		return uniqueNameRepositoryService.getUniqueValidName(expectedNewName, 
				destinationFolders, Optional.of(document), uniqueNameGenerator)
			.orElse(expectedNewName);
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
	
	public void moveWithUniqueName(NodeRef node, NodeRef destinationFolder) {
		logger.debug("Move node {} to {} : {} with unique name", node, destinationFolder, getPath(destinationFolder));
		uniqueNameRepositoryService.moveWithUniqueName(node, destinationFolder);
	}
	
	public void renameAndMoveNode(NodeRef node, NodeRef destinationFolder, String newName) {
		logger.debug("Move node {} to {} : {} with new name {}", node, destinationFolder, getPath(destinationFolder), newName);
		uniqueNameRepositoryService.moveWithUniqueName(node, newName, destinationFolder);
	}
	
	public void createFileLink(NodeRef nodeRef, NodeRef destinationFolder, Optional<String> linkNameOpt) {
		String linkName = linkNameOpt.isPresent() ? linkNameOpt.get() 
				: "Link to " + nodeModelRepositoryService.getProperty(nodeRef, CmModel.object.name);
		Map<QName, Serializable> properties = new HashMap<>();
		conversionService.setProperty(properties, AppModel.fileLink.destination, nodeRef);
		nodeModelRepositoryService.createNode(destinationFolder, AppModel.fileLink, linkName, properties);
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

	public NodeRef subFolder(String folderName, NodeRef destinationFolder) {
		String cleanFolderName = uniqueNameRepositoryService.toValidName(folderName, " ");
		if (logger.isDebugEnabled()) {
			logger.debug("Create subfolder {}", cleanFolderName);
		}
		
		String cacheKey = destinationFolder + "/" + cleanFolderName;
		return subFolderCache.get(nodeModelRepositoryService, cacheKey, 
				() -> nodeModelRepositoryService.getChildByName(destinationFolder, cleanFolderName),
				() -> {

			NodeRef newFolderRef;
			if (createSubFolderInInnerTransaction) {
				try {
					// Execute dans une sous transaction. Sinon, une éventuelle DuplicateChildNodeNameException rollback la transaction en cours.
					newFolderRef = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<NodeRef>() {
						@Override
						public NodeRef execute() {
							return nodeModelRepositoryService.createNode(destinationFolder, CmModel.folder, cleanFolderName);
						}
					}, false, true);
				} catch (DuplicateChildNodeNameRemoteException ex) {
					// si un autre processus a crée le même répertoire entre temps, on recommence le fait de le chercher
					newFolderRef = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<NodeRef>() {
						@Override
						public NodeRef execute() {
							Optional<NodeRef> childByName = nodeModelRepositoryService.getChildByName(destinationFolder, cleanFolderName);
							return childByName.get();
						}
					}, true, true);
				}
			} else {
				newFolderRef = nodeModelRepositoryService.createNode(destinationFolder, CmModel.folder, cleanFolderName);
			}
			
			if (addDeleteIfEmptyAspect) {
				nodeModelRepositoryService.addAspect(newFolderRef, OwsiModel.deleteIfEmpty);
			}
			return newFolderRef;
		});
	} 
	
	@Override
	public void clearCaches() {
		queryCache.clear();
		pathCache.clear();
		subFolderCache.clear();
	}
	
	@Override
	public ClassificationBuilder getBuilderForValues(Map<QName, Serializable> values) {
		return new ClassificationBuilder(this, new ClassificationEvent(values));
	}
	
	@Override
	public void registerTreeAspect(AspectModel container) {
		treeAspectService.registerAspect(conversionService.getRequired(container.getNameReference()));
	}
	@Override
	public void registerCopyPropertyCmName(AspectModel aspectOfRootNode, PropertyModel<String> propertyToCopy) {
		registerCopyProperty(aspectOfRootNode, propertyToCopy, CmModel.object.name);
	}
	@Override
	public <T extends Serializable> void registerCopyProperty(AspectModel aspectOfRootNode,
			PropertyModel<T> propertyToCopy, PropertyModel<T> propertyWhereCopy) {
		registerRootPropertyName.registerCopyProperty(
				conversionService.getRequired(aspectOfRootNode.getNameReference()), 
				conversionService.getRequired(propertyToCopy.getNameReference()), 
				conversionService.getRequired(propertyWhereCopy.getNameReference()));
	}
	
	@Override
	public void registerChildAspectForFolder(ContainerModel parentAspect, ContainerModel childAspect) {
		childAspectService.registerChildAspectForFolder(
				conversionService.getRequired(parentAspect.getNameReference()), 
				conversionService.getRequired(childAspect.getNameReference()));
	}
	@Override
	public void registerChildAspectForContent(ContainerModel parentAspect, ContainerModel childAspect) {
		childAspectService.registerChildAspectForContent(
				conversionService.getRequired(parentAspect.getNameReference()), 
				conversionService.getRequired(childAspect.getNameReference()));
	}
	
	
	public List<NodeRef> searchReference(RestrictionBuilder restrictionBuilder) {
		// Pas de cache
		return nodeSearchModelService.searchReference(restrictionBuilder); 
	}
	public Optional<NodeRef> searchUniqueReference(RestrictionBuilder restrictionBuilder) {
		String cacheKey = restrictionBuilder.toFtsQuery();
		return queryCache.get(nodeModelRepositoryService, cacheKey, 
				() -> nodeSearchModelService.searchReferenceUnique(restrictionBuilder));
	}

	public NodeModelRepositoryService getNodeModelService() {
		return nodeModelRepositoryService;
	}
	public PermissionRepositoryService getPermissionRepositoryService() {
		return permissionRepositoryService;
	}
	public ConversionService getConversionService() {
		return conversionService;
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

	
	
	public Optional<NodeRef> getPreviousWith(AbstractClassificationBuilder<?> builder, SinglePropertyModel<?>[] properties) {
		RestrictionBuilder restrictionBuilder = new RestrictionBuilder();
		for (SinglePropertyModel<?> property : properties) {
			addEq(builder, restrictionBuilder, property);
		}
		List<NodeRef> results = nodeSearchModelService.searchReference(restrictionBuilder);
		results.remove(builder.getNodeRef());
		if (results.size() > 1) {
			throw new IllegalStateException(results.size() + " results, expected 0 or 1. Query : " + restrictionBuilder.toFtsQuery());
		} else if (results.size() == 1) {
			return Optional.of(results.get(0));
		} else {
			return Optional.empty();
		}
	}
	private <T extends Serializable> void addEq(
			AbstractClassificationBuilder<?> restrictionBuilder, 
			RestrictionBuilder restrictions, 
			SinglePropertyModel<T> property) {
		T value = restrictionBuilder.getProperty(property);
		restrictions.eq(property, value);
	}
	
	public void newContentVersion(NodeRef source, NodeRef target, PropertyModel<?>[] propertiesToCopy) {
		for (PropertyModel<?> property : propertiesToCopy) {
			nodeModelRepositoryService.copyProperty(source, target, property);
		}
		
		ContentReader reader = contentService.getReader(source, ContentModel.PROP_CONTENT);
		if (reader != null) {
			ContentWriter writer = contentService.getWriter(target, ContentModel.PROP_CONTENT, true);
			writer.putContent(reader);
		}
	}
}
