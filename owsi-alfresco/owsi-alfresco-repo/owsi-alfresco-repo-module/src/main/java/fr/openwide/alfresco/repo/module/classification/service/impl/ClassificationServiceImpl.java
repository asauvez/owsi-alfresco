package fr.openwide.alfresco.repo.module.classification.service.impl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.alfresco.repo.node.NodeServicePolicies.OnAddAspectPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnUpdatePropertiesPolicy;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.DuplicateChildNodeNameException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.google.common.base.Optional;

import fr.openwide.alfresco.api.core.node.exception.NoSuchNodeRemoteException;
import fr.openwide.alfresco.api.core.node.model.RepositoryChildAssociation;
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
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.component.model.repository.model.SysModel;
import fr.openwide.alfresco.component.model.search.model.SearchQueryBuilder;
import fr.openwide.alfresco.component.model.search.model.restriction.RestrictionBuilder;
import fr.openwide.alfresco.component.model.search.service.NodeSearchModelService;
import fr.openwide.alfresco.repo.dictionary.node.service.NodeModelRepositoryService;
import fr.openwide.alfresco.repo.dictionary.policy.service.PolicyRepositoryService;
import fr.openwide.alfresco.repo.module.classification.model.ClassificationBuilder;
import fr.openwide.alfresco.repo.module.classification.model.ClassificationEvent;
import fr.openwide.alfresco.repo.module.classification.model.ClassificationMode;
import fr.openwide.alfresco.repo.module.classification.model.ClassificationPolicy;
import fr.openwide.alfresco.repo.module.classification.service.ClassificationService;
import fr.openwide.alfresco.repository.core.node.model.PreNodeCreationCallback;
import fr.openwide.alfresco.repository.core.node.service.NodeRepositoryService;
import fr.openwide.alfresco.repository.remote.conversion.service.ConversionService;

public class ClassificationServiceImpl implements ClassificationService, InitializingBean, 
		OnAddAspectPolicy, OnUpdatePropertiesPolicy, PreNodeCreationCallback {
	
	private final Logger logger = LoggerFactory.getLogger(ClassificationServiceImpl.class);
	
	private NodeModelRepositoryService nodeModelService;
	private NodeRepositoryService nodeRepositoryService;
	private NodeSearchModelService nodeSearchModelService;
	private PolicyRepositoryService policyRepositoryService;
	
	private ConversionService conversionService;
	private TransactionService transactionService;
	private DictionaryService dictionaryService;

	private Map<NameReference, ClassificationPolicy<?>> policies = new LinkedHashMap<>();
	private Map<NameReference, ContainerModel> models = new ConcurrentHashMap<>();

	private Map<String, NodeReference> queryCache = new ConcurrentHashMap<>();
	private Map<String, NodeReference> pathCache = new ConcurrentHashMap<>();

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
	public void reclassify(ContainerModel model) {
		reclassify(model, 100);
	}
	
	@Override
	public void reclassify(ContainerModel model, final int batchSize) {
		final RestrictionBuilder restriction = new RestrictionBuilder();
		if (model instanceof TypeModel) {
			restriction.isType((TypeModel) model);
		} else {
			restriction.hasAspect((AspectModel) model);
		}
		
		logger.info("Begin reclassify of " + model);
		
		for (int batchNumber = 0; ; batchNumber ++) {
			logger.info("*** Reclassify batch " + batchNumber + " ***");
			final int firstResult = batchNumber * batchSize;
			int listSize = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Integer>() {
				@Override
				public Integer execute() {
					List<BusinessNode> list = nodeSearchModelService.search(new SearchQueryBuilder()
							.restriction(restriction)
							.firstResult(firstResult)
							.maxResults(batchSize)
							.nodeScopeBuilder(new NodeScopeBuilder().nodeReference())
							.sort().asc(SysModel.referenceable.nodeUuid));
					int nodeNumber = 0;
					for (BusinessNode node : list) {
						if (logger.isDebugEnabled()) {
							logger.debug("Reclassify node " + (nodeNumber ++) + " / " + list.size());
						}
						classify(conversionService.getRequired(node.getNodeReference()), ClassificationMode.RECLASSIFY);
					}
					return list.size();
				}
			}, false, true);
			
			if (listSize < batchSize) break;
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		policyRepositoryService.onAddAspect(OwsiModel.classifiable, NotificationFrequency.TRANSACTION_COMMIT, this);
		policyRepositoryService.onUpdateProperties(OwsiModel.classifiable, NotificationFrequency.TRANSACTION_COMMIT, this);
		
		nodeRepositoryService.addPreNodeCreationCallback(this);
	}

	@Override
	public void onAddAspect(NodeRef nodeRef, QName aspectTypeQName) {
		classify(nodeRef, ClassificationMode.CREATE);
	}
	@Override
	public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		// Appeler à la création. Sera gérer par onAddAspect()
		if (! before.isEmpty()) {
			classify(nodeRef, ClassificationMode.UPDATE);
		}
	}
	
	/** 
	 * Si un service demande à créer un noeud sans spécifier de parent et qu'une politique de classement est défini
	 * pour son type, on le copie dans le home folder puis on le classe.
	 */
	@Override
	public void onPreNodeCreationCallback(RepositoryNode node) {
		if (node.getPrimaryParentAssociation() == null) {
			TypeDefinition type = dictionaryService.getType(conversionService.getRequired(node.getType()));
			boolean isclassifiable = node.getAspects().contains(OwsiModel.classifiable);
			QName classifiable = conversionService.getRequired(OwsiModel.classifiable.getNameReference());
			if (! isclassifiable) {
				for (AspectDefinition aspect : type.getDefaultAspects(true)) {
					if (classifiable.equals(aspect.getName())) {
						isclassifiable = true;
						break;
					}
				}
			}
			if (isclassifiable) {
				Optional<NodeReference> homeFolder = getHomeFolder();
				if (homeFolder.isPresent()) {
					if (logger.isDebugEnabled()) {
						logger.debug("Node without parent is assigned to current user {} home folder. "
								+ "Will then be moved then to the classify folder.", AuthenticationUtil.getRunAsUser());
					}
					node.setPrimaryParentAssociation(new RepositoryChildAssociation(
							new RepositoryNode(homeFolder.get()), 
							CmModel.folder.contains.getNameReference()));
				} else {
					logger.error("Node without parent has aspect {}, but user {} does not have a home folder.", 
							OwsiModel.classifiable, AuthenticationUtil.getRunAsUser());
				}
			}
		}
	}

	public Optional<NodeReference> getHomeFolder() {
		return nodeModelService.getUserHome();
	}
	
	private void classify(NodeRef nodeRef, ClassificationMode mode) {
		NodeReference nodeReference = conversionService.get(nodeRef);
		NameReference type = getPolicy(nodeReference);
		if (type == null) {
			throw new IllegalStateException("Can't find a policy to classify " + type);
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
			logger.error("Error during classify of " + nodeReference + " of type " + type, ex);
			throw ex;
		}
	}
	
	private String getPath(NodeReference nodeReference) {
		return nodeModelService.get(nodeReference, new NodeScopeBuilder().path()).getPath();
	}

	public void moveNode(NodeReference node, NodeReference destinationFolder) {
		if (logger.isDebugEnabled()) {
			logger.debug("Move node {} to {} : {}.", node.getReference(), destinationFolder, getPath(destinationFolder));
		}
		nodeModelService.moveNode(node, destinationFolder);
	}
	public void copyNode(NodeReference node, NodeReference destinationFolder) {
		if (logger.isDebugEnabled()) {
			logger.debug("Copy node {} to {} : {}.", node.getReference(), destinationFolder, getPath(destinationFolder));
		}
		nodeModelService.copy(node, destinationFolder);
	}
	public void createLink(NodeReference node, NodeReference destinationFolder) {
		if (logger.isDebugEnabled()) {
			logger.debug("Add link from node {} to {} : {}.", node.getReference(), destinationFolder, getPath(destinationFolder));
		}
		nodeModelService.addChild(node, destinationFolder);
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

	public NodeReference subFolder(final BusinessNode folderNode, NodeReference destinationFolder) {
		ChildAssociationModel associationType = CmModel.folder.contains;
		
		String folderName = folderNode.properties().getName();
		Optional<NodeReference> subFolderRef = nodeModelService.getChildByName(destinationFolder, folderName, associationType);
		if (subFolderRef.isPresent()) {
			return subFolderRef.get();
		} else {
			if (folderNode.getRepositoryNode().getType() == null) {
				folderNode.getRepositoryNode().setType(CmModel.folder.getNameReference());
			}
			if (folderNode.properties().getTitle() == null) {
				folderNode.properties().title(folderName);
			}
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
		}
	} 
	
	public Optional<NodeReference> searchUniqueReference(RestrictionBuilder restrictionBuilder) {
		return nodeSearchModelService.searchUniqueReference(restrictionBuilder);
	}

	public Optional<NodeReference> searchUniqueReferenceCached(RestrictionBuilder restrictionBuilder) {
		String query = restrictionBuilder.toQuery();
		NodeReference nodeReference = queryCache.get(query);
		if (nodeReference == null) {
			Optional<NodeReference> optional = searchUniqueReference(restrictionBuilder);
			if (optional.isPresent()) {
				queryCache.put(query, optional.get());
			}
			return optional;
		} else {
			// Vérifie juste que la node existe toujours
			if (! nodeModelService.exists(nodeReference)) {
				queryCache.remove(query);
				return Optional.absent();
			}
			return Optional.of(nodeReference);
		}
	}

	public Optional<NodeReference> getByNamedPath(String ... names) {
		return nodeModelService.getByNamedPath(names);
	}
	public Optional<NodeReference> getByNamedPathCached(String ... names) {
		String cacheKey = Arrays.toString(names);
		NodeReference nodeReference = pathCache.get(cacheKey);
		if (nodeReference == null) {
			Optional<NodeReference> optional = getByNamedPath(names);
			if (optional.isPresent()) {
				pathCache.put(cacheKey, optional.get());
			}
			return optional;
		} else {
			// Vérifie juste que la node existe toujours
			if (! nodeModelService.exists(nodeReference)) {
				pathCache.remove(cacheKey);
				return Optional.absent();
			}
			return Optional.of(nodeReference);
		}
	}
	
	public void setNodeModelService(NodeModelRepositoryService nodeModelService) {
		this.nodeModelService = nodeModelService; 
	}
	public void setNodeRepositoryService(NodeRepositoryService nodeRepositoryService) {
		this.nodeRepositoryService = nodeRepositoryService;
	}
	public void setNodeSearchModelService(NodeSearchModelService nodeSearchModelService) {
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

}
