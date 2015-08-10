package fr.openwide.alfresco.repo.dictionary.classification.service.impl;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.DuplicateChildNodeNameException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.springframework.beans.factory.InitializingBean;

import com.google.common.base.Optional;

import fr.openwide.alfresco.api.core.node.exception.NoSuchNodeRemoteException;
import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.component.model.repository.model.cm.CmContent;
import fr.openwide.alfresco.component.model.search.model.restriction.RestrictionBuilder;
import fr.openwide.alfresco.component.model.search.service.NodeSearchModelService;
import fr.openwide.alfresco.repo.dictionary.classification.model.ClassificationBuilder;
import fr.openwide.alfresco.repo.dictionary.classification.model.ClassificationPolicy;
import fr.openwide.alfresco.repo.dictionary.classification.model.SubFolderBuilder;
import fr.openwide.alfresco.repo.dictionary.classification.service.ClassificationService;
import fr.openwide.alfresco.repo.dictionary.model.OwsiModel;
import fr.openwide.alfresco.repo.dictionary.node.service.NodeModelRepositoryService;
import fr.openwide.alfresco.repository.remote.conversion.service.ConversionService;

public class ClassificationServiceImpl implements ClassificationService, InitializingBean {
	
	private NodeModelRepositoryService nodeModelService;
	private NodeSearchModelService nodeSearchModelService;
	private ConversionService conversionService;
	private TransactionService transactionService;

	private Map<NameReference, ClassificationPolicy<?>> policies = new LinkedHashMap<>();
	private Map<NameReference, ContainerModel> models = new ConcurrentHashMap<>();
	
	private Map<String, NodeReference> queryCache = new ConcurrentHashMap<>();

	@Override
	public void afterPropertiesSet() throws Exception {
		nodeModelService.bindClassBehaviour(OwsiModel.classifiable, new NodeServicePolicies.OnAddAspectPolicy() {
			@Override
			public void onAddAspect(NodeRef nodeRef, QName aspectTypeQName) {
				fileDocument(nodeRef, true);
			}
		}, NotificationFrequency.EVERY_EVENT);
		
		nodeModelService.bindClassBehaviour(OwsiModel.classifiable, new NodeServicePolicies.OnUpdatePropertiesPolicy() {
			@Override
			public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
				fileDocument(nodeRef, true);
			}
		}, NotificationFrequency.EVERY_EVENT);
	}
	
	@Override
	public <T extends ContainerModel> void addClassification(T model, ClassificationPolicy<T> policy) {
		ClassificationPolicy<?> old = policies.put(model.getNameReference(), policy);
		if (old != null) {
			throw new IllegalStateException("There is at least two policies for " + model.getNameReference());
		}
		models.put(model.getNameReference(), model);
	}
	
	private void fileDocument(NodeRef nodeRef, boolean update) {
		NodeReference nodeReference = conversionService.get(nodeRef);
		NameReference type = getPolicy(nodeReference);
		@SuppressWarnings("unchecked")
		ClassificationPolicy<ContainerModel> policy = (ClassificationPolicy<ContainerModel>) policies.get(type);
		ContainerModel model = models.get(type);
		
		BusinessNode node = getNode(nodeRef, model);
		if (node == null) {
			return;
		}
		
		ClassificationBuilder builder = new ClassificationBuilder(this, node);
		policy.classify(builder, model, node, update);
		NodeReference destinationFolder = builder.getDestinationFolder();
		
		if (destinationFolder != null && ! destinationFolder.equals(node.assocs().primaryParent().getNodeReference())) {
			nodeModelService.moveNode(node.getNodeReference(), destinationFolder);
		}
	}
	
	private NameReference getPolicy(NodeReference nodeReference) {
		NameReference type = nodeModelService.getType(nodeReference);
		ClassificationPolicy<?> policy = policies.get(type);
		if (policy != null) {
			return type;
		}
		for (NameReference aspect : policies.keySet()) {
			if (nodeModelService.hasAspect(nodeReference, aspect)) {
				return aspect;
			}
		}
		throw new IllegalStateException("Can't find a policy to classify " + type);
	}
	
	private BusinessNode getNode(NodeRef nodeRef, ContainerModel model) {
		try {
			NodeScopeBuilder nodeScopeBuilder = new NodeScopeBuilder()
					.nodeReference()
					.properties().set(CmModel.object)
					.properties().set(model);
			nodeScopeBuilder.assocs().primaryParent().nodeReference();
			return nodeModelService.get(conversionService.get(nodeRef), nodeScopeBuilder);
		} catch (NoSuchNodeRemoteException ex) {
			return null;
		}
	}
	
	public NodeReference subFolder(final BusinessNode folderNode, NodeReference destinationFolder) {
		String folderName = folderNode.properties().getName();
		Optional<NodeReference> subFolderRef = nodeModelService.getChildByName(destinationFolder, folderName);
		if (subFolderRef.isPresent()) {
			return subFolderRef.get();
		} else {
			if (folderNode.getRepositoryNode().getType() == null) {
				folderNode.getRepositoryNode().setType(CmModel.folder.getNameReference());
			}
			if (folderNode.properties().getTitle() == null) {
				folderNode.properties().title(folderName);
			}
			folderNode.assocs().primaryParent().nodeReference(destinationFolder);
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
				return nodeModelService.getChildByName(destinationFolder, folderName).get();
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
	
	public void setNodeModelService(NodeModelRepositoryService nodeModelService) {
		this.nodeModelService = nodeModelService; 
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
	
	
	private void toto() {
		addClassification(CmModel.content, new ClassificationPolicy<CmContent>() {
			@Override
			public void classify(ClassificationBuilder builder, CmContent model, BusinessNode node, boolean update) {
				builder
					.rootFolderIdentifier(NameReference.create("metier", "rootFolder"))
					.subFolder("toto")
					.subFolder(new SubFolderBuilder(model.auditable.creator))
					.subFolderYear()
					.subFolderMonth();
			}
		});
	}
	
}
