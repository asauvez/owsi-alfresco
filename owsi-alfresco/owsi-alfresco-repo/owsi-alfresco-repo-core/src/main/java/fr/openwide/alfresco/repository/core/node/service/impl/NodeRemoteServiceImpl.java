package fr.openwide.alfresco.repository.core.node.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.rendition.RenditionService;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.DuplicateChildNodeNameException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AccessPermission;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.service.transaction.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.alfresco.api.core.authority.model.RepositoryAuthority;
import fr.openwide.alfresco.api.core.node.binding.content.NodeContentDeserializer;
import fr.openwide.alfresco.api.core.node.binding.content.NodeContentSerializationComponent;
import fr.openwide.alfresco.api.core.node.binding.content.NodeContentSerializer;
import fr.openwide.alfresco.api.core.node.exception.DuplicateChildNodeNameRemoteException;
import fr.openwide.alfresco.api.core.node.exception.NoSuchNodeRemoteException;
import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.node.model.RepositoryAccessControl;
import fr.openwide.alfresco.api.core.node.model.RepositoryChildAssociation;
import fr.openwide.alfresco.api.core.node.model.RepositoryContentData;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.node.model.RepositoryPermission;
import fr.openwide.alfresco.api.core.remote.exception.AccessDeniedRemoteException;
import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.repository.core.node.model.PreNodeCreationCallback;
import fr.openwide.alfresco.repository.core.node.service.NodeRepositoryService;
import fr.openwide.alfresco.repository.core.node.web.script.NodeContentCallback;
import fr.openwide.alfresco.repository.core.node.web.script.NodeContentHolder;
import fr.openwide.alfresco.repository.remote.conversion.service.ConversionService;
import fr.openwide.alfresco.repository.remote.framework.exception.InvalidPayloadException;

public class NodeRemoteServiceImpl implements NodeRepositoryService {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	private Map<Class<?>, NodeContentSerializer<?>> serializersByClass = NodeContentSerializationComponent.getDefaultSerializersByClass();
	private List<PreNodeCreationCallback> preNodeCreationCallbacks = new ArrayList<>();
	
	private NodeService nodeService;
	private ContentService contentService;
	private PermissionService permissionService;
	private RenditionService renditionService;
	private TransactionService transactionService;

	private ConversionService conversionService;

	@Override
	public RepositoryNode get(NodeReference nodeReference, NodeScope scope) throws NoSuchNodeRemoteException {
		return getRepositoryNode(conversionService.getRequired(nodeReference), scope);
	}

	@Override
	public List<RepositoryNode> getChildren(
			NodeReference nodeReference, NameReference childAssocTypeName, NodeScope scope) {
		List<ChildAssociationRef> assocs = nodeService.getChildAssocs(
				conversionService.getRequired(nodeReference), 
				conversionService.getRequired(childAssocTypeName), 
				RegexQNamePattern.MATCH_ALL, true);
		List<RepositoryNode> res = new ArrayList<>();
		for (ChildAssociationRef assoc : assocs) {
			res.add(getRepositoryNode(assoc.getChildRef(), scope));
		}
		return res;
	}
	private List<RepositoryNode> getParent(
			NodeReference nodeReference, NameReference childAssocTypeName, NodeScope scope) {
		List<ChildAssociationRef> assocs = nodeService.getParentAssocs(
				conversionService.getRequired(nodeReference), 
				conversionService.getRequired(childAssocTypeName), 
				RegexQNamePattern.MATCH_ALL);
		List<RepositoryNode> res = new ArrayList<>();
		for (ChildAssociationRef assoc : assocs) {
			res.add(getRepositoryNode(assoc.getParentRef(), scope));
		}
		return res;
	}

	@Override
	public List<RepositoryNode> getTargetAssocs(
			NodeReference nodeReference, NameReference assocName, NodeScope scope) {
		List<AssociationRef> assocs = nodeService.getTargetAssocs(
				conversionService.getRequired(nodeReference), 
				conversionService.getRequired(assocName));
		List<RepositoryNode> res = new ArrayList<>();
		for (AssociationRef assoc : assocs) {
			res.add(getRepositoryNode(assoc.getTargetRef(), scope));
		}
		return res;
	}

	@Override
	public List<RepositoryNode> getSourceAssocs(
			NodeReference nodeReference, NameReference assocName, NodeScope scope) {
		List<AssociationRef> assocs = nodeService.getSourceAssocs(
				conversionService.getRequired(nodeReference), 
				conversionService.getRequired(assocName));
		List<RepositoryNode> res = new ArrayList<>();
		for (AssociationRef assoc : assocs) {
			res.add(getRepositoryNode(assoc.getSourceRef(), scope));
		}
		return res;
	}

	protected RepositoryNode getRepositoryNode(final NodeRef nodeRef, NodeScope scope) throws NoSuchNodeRemoteException {
		NodeReference nodeReference = conversionService.get(nodeRef);
		if (! nodeService.exists(nodeRef)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("No such node {}", nodeReference);
			}
			throw new NoSuchNodeRemoteException(nodeReference.getReference());
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Loading node {}", nodeReference);
		}

		RepositoryNode node = new RepositoryNode();
		if (scope.isNodeReference()) {
			node.setNodeReference(nodeReference);
		}
		if (scope.isPath()) {
			node.setPath(nodeService.getPath(nodeRef).toString());
		}
		if (scope.isType()) {
			node.setType(conversionService.get(nodeService.getType(nodeRef)));
		}
		NodeScope primaryParentScope = scope.getPrimaryParent();
		if (scope.isRecursivePrimaryParent()) {
			if (primaryParentScope != null) {
				throw new IllegalArgumentException("You can't specify both a primary parent scope and a recursive primary parent.");
			}
			primaryParentScope = scope;
		}
		if (primaryParentScope != null) {
			ChildAssociationRef primaryParent = nodeService.getPrimaryParent(nodeRef);
			if (primaryParent.getParentRef() != null) {
				node.setPrimaryParentAssociation(new RepositoryChildAssociation(
						getRepositoryNode(primaryParent.getParentRef(), primaryParentScope),
						conversionService.get(primaryParent.getTypeQName())));
			}
		}
		
		for (NameReference property : scope.getProperties()) {
			Serializable value = nodeService.getProperty(nodeRef, conversionService.getRequired(property));
			if (value != null) {
				node.getProperties().put(property, conversionService.getForApplication(value));
			}
		}
		for (Entry<NameReference, NodeContentDeserializer<?>> entry : scope.getContentDeserializers().entrySet()) {
			NameReference contentProperty = entry.getKey();
			ContentReader reader = contentService.getReader(nodeRef, conversionService.getRequired(contentProperty));
			if (reader != null) {
				NodeContentDeserializer<?> deserializer = entry.getValue();
				if (deserializer == null) {
					// Appel depuis un Web Script
					node.getContents().put(contentProperty, reader);
				} else {
					// Appel depuis Alfresco
					try (InputStream inputStream = reader.getContentInputStream()) {
						Object value = deserializer.deserialize(node, contentProperty, inputStream);
						node.getContents().put(contentProperty, value);
					} catch (IOException e) {
						throw new IllegalStateException(e);
					}
				}
			}
		}
		for (NameReference aspect : scope.getAspects()) {
			if (nodeService.hasAspect(nodeRef, conversionService.getRequired(aspect))) {
				node.getAspects().add(aspect);
			}
		}
		
		// get associations
		for (final Entry<NameReference, NodeScope> entry : scope.getRenditions().entrySet()) {
			ChildAssociationRef renditionRef = renditionService.getRenditionByName(nodeRef, conversionService.getRequired(entry.getKey()));
			if (renditionRef == null) {
				// On est dans un read-only. On a donc besoin d'une inner transaction pour générer la rendition
				renditionRef = transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<ChildAssociationRef>() {
					@Override
					public ChildAssociationRef execute() throws Throwable {
						return renditionService.render(nodeRef, conversionService.getRequired(entry.getKey()));
					}
				}, false, true);
			}
			node.getRenditions().put(
					entry.getKey(), 
					getRepositoryNode(renditionRef.getChildRef(), entry.getValue()));
		}
		for (Entry<NameReference, NodeScope> entry : scope.getChildAssociations().entrySet()) {
			node.getChildAssociations().put(
					entry.getKey(), 
					getChildren(nodeReference, entry.getKey(), entry.getValue()));
		}
		for (Entry<NameReference, NodeScope> entry : scope.getParentAssociations().entrySet()) {
			node.getParentAssociations().put(
					entry.getKey(), 
					getParent(nodeReference, entry.getKey(), entry.getValue()));
		}
		for (Entry<NameReference, NodeScope> entry : scope.getTargetAssocs().entrySet()) {
			node.getTargetAssocs().put(
					entry.getKey(), 
					getTargetAssocs(nodeReference, entry.getKey(), entry.getValue()));
		}
		for (Entry<NameReference, NodeScope> entry : scope.getSourceAssocs().entrySet()) {
			node.getSourceAssocs().put(
					entry.getKey(), 
					getSourceAssocs(nodeReference, entry.getKey(), entry.getValue()));
		}
		
		// get recursive associations
		for (NameReference association : scope.getRecursiveChildAssociations()) {
			node.getChildAssociations().put(
					association, 
					getChildren(nodeReference, association, scope));
		}
		for (NameReference association : scope.getRecursiveParentAssociations()) {
			node.getParentAssociations().put(
					association, 
					getParent(nodeReference, association, scope));
		}
		
		// get permissions
		for (RepositoryPermission permission : scope.getUserPermissions()) {
			if (permissionService.hasPermission(nodeRef, permission.getName()) == AccessStatus.ALLOWED) {
				node.getUserPermissions().add(permission);
			}
		}
		if (scope.isAccessPermissions()) {
			node.setInheritParentPermissions(permissionService.getInheritParentPermissions(nodeRef));
			for (AccessPermission accessPermission : permissionService.getAllSetPermissions(nodeRef)) {
				node.getAccessControlList().add(new RepositoryAccessControl(
						new RepositoryAuthority(accessPermission.getAuthority()),
						new RepositoryPermission(accessPermission.getPermission()),
						accessPermission.getAccessStatus() == AccessStatus.ALLOWED));
			}
		}
		return node;
	}

	protected void validateCreate(RepositoryNode node) {
		if (node.getNodeReference() != null) {
			throw new InvalidPayloadException("Node already has a reference");
		}
		RepositoryChildAssociation primaryParent = node.getPrimaryParentAssociation();
		if (primaryParent == null || primaryParent.getParentNode() == null) {
			throw new InvalidPayloadException("A primary parent association is required.");
		}
		String cmName = node.getProperty(conversionService.get(ContentModel.PROP_NAME), String.class);
		if (cmName == null) {
			throw new InvalidPayloadException("Property is required: " + conversionService.get(ContentModel.PROP_NAME));
		}
	}

	@Override
	public void addPreNodeCreationCallback(PreNodeCreationCallback callback) {
		preNodeCreationCallbacks.add(callback);
	}

	@Override
	public List<NodeReference> create(List<RepositoryNode> nodes) throws DuplicateChildNodeNameRemoteException {
		List<NodeReference> nodesReferences = new ArrayList<>();
		for (RepositoryNode node : nodes) {
			nodesReferences.add(create(node));
		}
		return nodesReferences;
	}
	
	public static QName createAssociationName(String nodeName) {
		return QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(nodeName));
	}
	
	protected NodeReference create(RepositoryNode node) throws DuplicateChildNodeNameRemoteException {
		for (PreNodeCreationCallback callback : preNodeCreationCallbacks) {
			callback.onPreNodeCreationCallback(node);
		}
		
		validateCreate(node);
		
		Map<QName, Serializable> properties = new LinkedHashMap<>();
		for (Entry<NameReference, Serializable> property : node.getProperties().entrySet()) {
			Serializable value = property.getValue();
			if (! (value instanceof RepositoryContentData)) {
				properties.put(
					conversionService.getRequired(property.getKey()), 
					conversionService.getForRepository(value));
			}
		}
		String cmName = node.getProperty(conversionService.get(ContentModel.PROP_NAME), String.class);
		RepositoryChildAssociation primaryParent = node.getPrimaryParentAssociation();
		try {
			NodeRef parentRef = conversionService.getRequired(primaryParent.getParentNode().getNodeReference());
			QName assocType = conversionService.getRequired(primaryParent.getType());
			QName assocName = createAssociationName(cmName);
			QName type = conversionService.getRequired(node.getType());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Creating node type {} in {}/{}/{}", type, parentRef, assocType, assocName);
			}
			NodeRef nodeRef = nodeService.createNode(parentRef, assocType, assocName, type, properties).getChildRef();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Created node {}", nodeRef);
			}
			NodeReference nodeReference = conversionService.get(nodeRef);
			node.setNodeReference(nodeReference);
			
			for (NameReference aspectName : node.getAspects()) {
				nodeService.addAspect(nodeRef, conversionService.getRequired(aspectName), null);
			}
			setContents(nodeRef, node, node.getContents().keySet());
			setPermissions(nodeRef, node);
			
			for (Entry<NameReference, List<RepositoryNode>> entry : node.getChildAssociations().entrySet()) {
				for (RepositoryNode childNode : entry.getValue()) {
					childNode.setPrimaryParentAssociation(new RepositoryChildAssociation(node, entry.getKey()));
					create(childNode);
				}
			}
			for (Entry<NameReference, List<RepositoryNode>> entry : node.getSourceAssocs().entrySet()) {
				for (RepositoryNode targetNode : entry.getValue()) {
					if (targetNode.getNodeReference() == null) {
						create(targetNode);
					}
					nodeService.createAssociation(
							nodeRef, 
							conversionService.getRequired(targetNode.getNodeReference()), 
							conversionService.getRequired(entry.getKey()));
				}
			}
			for (Entry<NameReference, List<RepositoryNode>> entry : node.getTargetAssocs().entrySet()) {
				for (RepositoryNode sourceNode : entry.getValue()) {
					if (sourceNode.getNodeReference() == null) {
						create(sourceNode);
					}
					nodeService.createAssociation(
							conversionService.getRequired(sourceNode.getNodeReference()), 
							nodeRef, 
							conversionService.getRequired(entry.getKey()));
				}
			}

			return nodeReference;
		} catch (DuplicateChildNodeNameException e) {
			throw new DuplicateChildNodeNameRemoteException(cmName, e);
		}
	}

	@Override
	public void update(List<RepositoryNode> nodes, NodeScope nodeScope) throws DuplicateChildNodeNameRemoteException {
		for (RepositoryNode node : nodes) {
			update(node, nodeScope);
		}
	}
	
	protected void update(RepositoryNode node, NodeScope nodeScope) throws DuplicateChildNodeNameRemoteException {
		String cmName = node.getProperty(conversionService.get(ContentModel.PROP_NAME), String.class);
		try {
			NodeRef nodeRef = conversionService.getRequired(node.getNodeReference());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Updating node {}", nodeRef);
			}
			if (nodeScope.isType()) {
				nodeService.setType(nodeRef, conversionService.getRequired(node.getType()));
			}
			if (nodeScope.getPrimaryParent() != null) {
				if (cmName == null) {
					cmName = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
				}
				RepositoryChildAssociation repoPrimaryParent = node.getPrimaryParentAssociation();
				NodeRef parentRef = conversionService.getRequired(repoPrimaryParent.getParentNode().getNodeReference());
				QName assocType = conversionService.getRequired(repoPrimaryParent.getType());
				QName assocName = createAssociationName(cmName);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Moving node {} to {}/{}/{}", nodeRef, parentRef, assocType, assocName);
				}
				nodeService.moveNode(nodeRef, parentRef, assocType, assocName);
			}
			for (NameReference propertyName : nodeScope.getProperties()) {
				Serializable value = node.getProperty(propertyName);
				if (value != null) {
					if (! (value instanceof RepositoryContentData)) {
						nodeService.setProperty(nodeRef, 
							conversionService.getRequired(propertyName), 
							conversionService.getForRepository(value));
					}
				} else {
					nodeService.removeProperty(nodeRef, 
						conversionService.getRequired(propertyName)); 
				}
			}
			for (NameReference aspectName : nodeScope.getAspects()) {
				if (node.getAspects().contains(aspectName)) {
					nodeService.addAspect(nodeRef, conversionService.getRequired(aspectName), null);
				} else {
					nodeService.removeAspect(nodeRef, conversionService.getRequired(aspectName));
				}
			}
			for (Entry<NameReference, NodeScope> entry : nodeScope.getChildAssociations().entrySet()) {
				for (RepositoryNode childNode : node.getChildAssociations().get(entry.getKey())) {
					childNode.setPrimaryParentAssociation(new RepositoryChildAssociation(node, entry.getKey()));
					saveOrUpdate(childNode, entry.getValue());
				}
			}
			for (Entry<NameReference, NodeScope> entry : nodeScope.getParentAssociations().entrySet()) {
				for (RepositoryNode childNode : node.getParentAssociations().get(entry.getKey())) {
					saveOrUpdate(childNode, entry.getValue());
				}
			}

			for (NameReference association : nodeScope.getRecursiveChildAssociations()) {
				for (RepositoryNode childNode : node.getChildAssociations().get(association)) {
					childNode.setPrimaryParentAssociation(new RepositoryChildAssociation(node, association));
					saveOrUpdate(childNode, nodeScope);
				}
			}
			for (NameReference association : nodeScope.getRecursiveParentAssociations()) {
				for (RepositoryNode childNode : node.getParentAssociations().get(association)) {
					saveOrUpdate(childNode, nodeScope);
				}
			}

			setContents(nodeRef, node, nodeScope.getContentDeserializers().keySet());
			
			if (nodeScope.isAccessPermissions()) {
				setPermissions(nodeRef, node);
			}
			if (! nodeScope.getUserPermissions().isEmpty()) {
				throw new IllegalStateException("You can't update the permissions of the current user on the node (userPermissions). To modify the node permissions for everyone, use accessPermissions");
			}
		} catch (DuplicateChildNodeNameException e) {
			throw new DuplicateChildNodeNameRemoteException(cmName, e);
		}
	}

	private void saveOrUpdate(RepositoryNode node, NodeScope nodeScope) throws DuplicateChildNodeNameRemoteException {
		if (node.getNodeReference() != null) {
			update(node, nodeScope);
		} else if (node.getPrimaryParentAssociation() != null) {
			create(node);
		} else {
			throw new InvalidPayloadException("A node reference or a primary parent association is required.");
		}
	}
	
	private void setPermissions(final NodeRef nodeRef, final RepositoryNode node) {
		// Check si on a les droits
		if (permissionService.hasPermission(nodeRef, PermissionService.CHANGE_PERMISSIONS) != AccessStatus.ALLOWED) {
			throw new AccessDeniedRemoteException("You don't have the permission to change the permissions on " + nodeRef);
		}
		
		AuthenticationUtil.runAs(new RunAsWork<Void>() {
			@Override
			public Void doWork() throws Exception {
				setPermissionsAsAdmin(nodeRef, node);
				return null;
			}
		}, AuthenticationUtil.getSystemUserName());
	}
	
	private void setPermissionsAsAdmin(NodeRef nodeRef, RepositoryNode node) {
		if (node.getInheritParentPermissions() != null) {
			permissionService.setInheritParentPermissions(nodeRef, node.getInheritParentPermissions());
		}
		
		Set<RepositoryAccessControl> oldPermissions = new HashSet<RepositoryAccessControl>();
		for (AccessPermission oldPermission : permissionService.getAllSetPermissions(nodeRef)) {
			if (oldPermission.isSetDirectly()) {
				oldPermissions.add(new RepositoryAccessControl(
					new RepositoryAuthority(oldPermission.getAuthority()),
					new RepositoryPermission(oldPermission.getPermission()),
					oldPermission.getAccessStatus() == AccessStatus.ALLOWED));
			}
		}
		
		for (RepositoryAccessControl newPermission : node.getAccessControlList()) {
			if (! oldPermissions.remove(newPermission)) {
				permissionService.setPermission(nodeRef, 
						newPermission.getAuthority().getName(), 
						newPermission.getPermission().getName(), 
						newPermission.isAllowed());
			}
		}
		
		for (RepositoryAccessControl oldPermission : oldPermissions) {
			permissionService.deletePermission(nodeRef, 
					oldPermission.getAuthority().getName(), 
					oldPermission.getPermission().getName());
		}
	}

	private void setContents(final NodeRef nodeRef, RepositoryNode node, Set<NameReference> contentProperties) {
		for (final NameReference contentProperty : contentProperties) {
			final RepositoryContentData contentData = node.getProperty(contentProperty, RepositoryContentData.class);
			
			Object contentValue = node.getContents().get(contentProperty);
			if (contentValue instanceof NodeContentHolder) {
				// Appel depuis un Web Script
				((NodeContentHolder) contentValue).setContentCallback(new NodeContentCallback() {
					@Override
					public void doWithInputStream(InputStream inputStream) {
						if (nodeService.exists(nodeRef)) {
							ContentWriter writer = contentService.getWriter(nodeRef, conversionService.getRequired(contentProperty), true);
							writer.putContent(inputStream);
	
							setContentData(nodeRef, contentProperty, 
									(contentData != null) ? contentData : new RepositoryContentData(), 
									writer);
						}
					}
				});
			} else if (contentValue != null) {
				// Appel depuis Alfresco
				@SuppressWarnings("unchecked")
				NodeContentSerializer<Object> serializer = (NodeContentSerializer<Object>) serializersByClass.get(contentValue.getClass());
				if (serializer == null) {
					throw new IllegalArgumentException(contentProperty + "/" + contentValue.getClass() +  " has no default serializer.");
				}
				ContentWriter writer = contentService.getWriter(nodeRef, conversionService.getRequired(contentProperty), true);
				try (OutputStream outputStream = writer.getContentOutputStream()) {
					serializer.serialize(node, contentProperty, contentValue, outputStream);
				} catch (IOException e) {
					throw new IllegalStateException(e);
				}
				setContentData(nodeRef, contentProperty, contentData, writer);
			} else {
				ContentWriter writer = contentService.getWriter(nodeRef, conversionService.getRequired(contentProperty), true);
				writer.putContent("");
			}
		}
	}

	private void setContentData(NodeRef nodeRef, NameReference contentProperty, RepositoryContentData contentData, ContentWriter writer) {
		if (contentData.getMimetype() != null) {
			writer.setMimetype(contentData.getMimetype());
		}
		if (contentData.getEncoding() != null) {
			writer.setEncoding(contentData.getEncoding());
		}
		if (contentData.getLocale() != null) {
			writer.setLocale(contentData.getLocale());
		}

		if (contentData.getMimetype() == null || contentData.getEncoding() == null) {
			if (contentData.getMimetype() == null) {
				String cmName = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
				writer.guessMimetype(cmName);
			}
			if (contentData.getEncoding() == null) {
				writer.guessEncoding();
			}
			// Nécessaire, car non mis à jour après putContent
			nodeService.setProperty(nodeRef, conversionService.getRequired(contentProperty), writer.getContentData());
		}
	}
	
	@Override
	public void delete(List<NodeReference> nodeReferences) {
		for (NodeReference nodeReference : nodeReferences) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Deleting node {}", nodeReference);
			}
			nodeService.deleteNode(conversionService.getRequired(nodeReference));
		}
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}
	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}
	public void setRenditionService(RenditionService renditionService) {
		this.renditionService = renditionService;
	}
	public void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}
}
