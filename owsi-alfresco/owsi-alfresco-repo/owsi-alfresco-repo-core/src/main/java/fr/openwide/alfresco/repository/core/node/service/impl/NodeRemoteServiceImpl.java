package fr.openwide.alfresco.repository.core.node.service.impl;

import java.io.InputStream;
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
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
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

import fr.openwide.alfresco.repository.api.node.exception.DuplicateChildNameException;
import fr.openwide.alfresco.repository.api.node.exception.NoSuchNodeException;
import fr.openwide.alfresco.repository.api.node.model.NodeScope;
import fr.openwide.alfresco.repository.api.node.model.RepositoryAuthority;
import fr.openwide.alfresco.repository.api.node.model.RepositoryAuthorityPermission;
import fr.openwide.alfresco.repository.api.node.model.RepositoryChildAssociation;
import fr.openwide.alfresco.repository.api.node.model.RepositoryContentData;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.node.model.RepositoryPermission;
import fr.openwide.alfresco.repository.api.node.service.NodeRemoteService;
import fr.openwide.alfresco.repository.api.remote.exception.AccessDeniedRemoteException;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;
import fr.openwide.alfresco.repository.core.node.web.script.AbstractNodeWebScript.ContentCallback;
import fr.openwide.alfresco.repository.core.remote.service.ConversionService;
import fr.openwide.alfresco.repository.remote.framework.exception.InvalidPayloadException;

public class NodeRemoteServiceImpl implements NodeRemoteService {

	private NodeService nodeService;
	private ContentService contentService;
	private PermissionService permissionService;

	private ConversionService conversionService;

	@Override
	public RepositoryNode get(NodeReference nodeReference, NodeScope scope) throws NoSuchNodeException {
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

	protected RepositoryNode getRepositoryNode(final NodeRef nodeRef, NodeScope scope) throws NoSuchNodeException {
		NodeReference nodeReference = conversionService.get(nodeRef);
		if (! nodeService.exists(nodeRef)) {
			throw new NoSuchNodeException(nodeReference.getReference());
		}
		RepositoryNode node = new RepositoryNode();
		if (scope.isNodeReference()) {
			node.setNodeReference(conversionService.get(nodeRef));
		}
		if (scope.isPath()) {
			node.setPath(nodeService.getPath(nodeRef).toString());
		}
		if (scope.isType()) {
			node.setType(conversionService.get(nodeService.getType(nodeRef)));
		}
		if (scope.getPrimaryParent() != null) {
			ChildAssociationRef primaryParent = nodeService.getPrimaryParent(nodeRef);
			if (primaryParent.getParentRef() != null) {
				node.setPrimaryParentAssociation(new RepositoryChildAssociation(
						getRepositoryNode(primaryParent.getParentRef(), scope.getPrimaryParent()),
						conversionService.get(primaryParent.getTypeQName())));
			}
		}
		for (NameReference property : scope.getProperties()) {
			Serializable value = nodeService.getProperty(nodeRef, conversionService.getRequired(property));
			if (value != null) {
				node.getProperties().put(property, conversionService.getForApplication(value));
			}
		}
		for (NameReference property : scope.getContentDeserializers().keySet()) {
			ContentReader reader = contentService.getReader(nodeRef, conversionService.getRequired(property));
			if (reader != null) {
				node.getContents().put(property, reader);
			}
		}
		for (NameReference aspect : scope.getAspects()) {
			if (nodeService.hasAspect(nodeRef, conversionService.getRequired(aspect))) {
				node.getAspects().add(aspect);
			}
		}
		
		// get associations
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
				node.getAccessPermissions().add(new RepositoryAuthorityPermission(
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
		String cmName = (String) node.getProperties().get(conversionService.get(ContentModel.PROP_NAME));
		if (cmName == null) {
			throw new InvalidPayloadException("Property is required: " + conversionService.get(ContentModel.PROP_NAME));
		}
	}
	
	@Override
	public List<NodeReference> create(List<RepositoryNode> nodes) throws DuplicateChildNameException {
		List<NodeReference> nodesReferences = new ArrayList<>();
		for (RepositoryNode node : nodes) {
			nodesReferences.add(create(node));
		}
		return nodesReferences;
	}
	
	protected NodeReference create(RepositoryNode node) throws DuplicateChildNameException {
		validateCreate(node);
		
		Map<QName, Serializable> properties = new LinkedHashMap<QName, Serializable>();
		for (Entry<NameReference, Serializable> property : node.getProperties().entrySet()) {
			Serializable value = property.getValue();
			if (! (value instanceof RepositoryContentData)) {
				properties.put(
					conversionService.getRequired(property.getKey()), 
					conversionService.getForRepository(value));
			}
		}
		String cmName = (String) node.getProperties().get(conversionService.get(ContentModel.PROP_NAME));
		RepositoryChildAssociation primaryParent = node.getPrimaryParentAssociation();
		try {
			NodeRef nodeRef = nodeService.createNode(
					conversionService.getRequired(primaryParent.getParentNode().getNodeReference()), 
					conversionService.getRequired(primaryParent.getType()), 
					QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, cmName.toLowerCase()), 
					conversionService.getRequired(node.getType()), 
					properties).getChildRef();
			NodeReference nodeReference = conversionService.get(nodeRef);
			node.setNodeReference(nodeReference);
			
			for (NameReference aspectName : node.getAspects()) {
				nodeService.addAspect(nodeRef, conversionService.getRequired(aspectName), null);
			}
			setContents(nodeRef, node);
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
			throw new DuplicateChildNameException(cmName, e);
		}
	}

	@Override
	public void update(List<RepositoryNode> nodes, NodeScope nodeScope) throws DuplicateChildNameException {
		for (RepositoryNode node : nodes) {
			update(node, nodeScope);
		}
	}
	
	protected void update(RepositoryNode node, NodeScope nodeScope) throws DuplicateChildNameException {
		String cmName = (String) node.getProperties().get(conversionService.get(ContentModel.PROP_NAME));
		try {
			NodeRef nodeRef = conversionService.getRequired(node.getNodeReference());
			if (nodeScope.isType()) {
				nodeService.setType(nodeRef, conversionService.getRequired(node.getType()));
			}
			if (nodeScope.getPrimaryParent() != null) {
				if (cmName == null) {
					cmName = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
				}
				RepositoryChildAssociation repoPrimaryParent = node.getPrimaryParentAssociation();
				nodeService.moveNode(nodeRef, 
						conversionService.getRequired(repoPrimaryParent.getParentNode().getNodeReference()), 
						conversionService.getRequired(repoPrimaryParent.getType()), 
						QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, cmName.toLowerCase()));
			}
			for (NameReference propertyName : nodeScope.getProperties()) {
				Serializable value = node.getProperties().get(propertyName);
				if (value != null) {
					if (! (value instanceof ContentData)) {
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

			setContents(nodeRef, node);
			if (nodeScope.isAccessPermissions()) {
				setPermissions(nodeRef, node);
			}
		} catch (DuplicateChildNodeNameException e) {
			throw new DuplicateChildNameException(cmName, e);
		}
	}

	private void saveOrUpdate(RepositoryNode node, NodeScope nodeScope) throws DuplicateChildNameException {
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
		
		Set<RepositoryAuthorityPermission> oldPermissions = new HashSet<RepositoryAuthorityPermission>();
		for (AccessPermission oldPermission : permissionService.getAllSetPermissions(nodeRef)) {
			if (oldPermission.isSetDirectly()) {
				oldPermissions.add(new RepositoryAuthorityPermission(
					new RepositoryAuthority(oldPermission.getAuthority()),
					new RepositoryPermission(oldPermission.getPermission()),
					oldPermission.getAccessStatus() == AccessStatus.ALLOWED));
			}
		}
		
		for (RepositoryAuthorityPermission newPermission : node.getAccessPermissions()) {
			if (! oldPermissions.remove(newPermission)) {
				permissionService.setPermission(nodeRef, 
						newPermission.getAuthority().getName(), 
						newPermission.getPermission().getName(), 
						newPermission.isAllowed());
			}
		}
		
		for (RepositoryAuthorityPermission oldPermission : oldPermissions) {
			permissionService.deletePermission(nodeRef, 
					oldPermission.getAuthority().getName(), 
					oldPermission.getPermission().getName());
		}
	}

	private void setContents(final NodeRef nodeRef, RepositoryNode node) {
		for (Entry<NameReference, Object> entry : node.getContents().entrySet()) {
			final RepositoryContentData contentData = (RepositoryContentData) node.getProperties().get(entry.getKey());
			
			entry.setValue(new ContentCallback() {
				@Override
				public void doWithInputStream(NameReference contentProperty, InputStream inputStream) {
					setContent(nodeRef, contentProperty, 
							(contentData != null) ? contentData : new RepositoryContentData(), 
							inputStream);
				}
			});
		}
	}

	protected void setContent(NodeRef nodeRef, NameReference contentProperty, RepositoryContentData contentData, 
			InputStream contentInputStream) {
		QName qname = conversionService.getRequired(contentProperty);
		ContentWriter writer = contentService.getWriter(nodeRef, qname, true);
		if (contentData.getMimetype() != null) {
			writer.setMimetype(contentData.getMimetype());
		}
		if (contentData.getEncoding() != null) {
			writer.setEncoding(contentData.getEncoding());
		}
		if (contentData.getLocale() != null) {
			writer.setLocale(contentData.getLocale());
		}
		
		writer.putContent(contentInputStream);
		
		if (contentData.getMimetype() == null || contentData.getEncoding() == null) {
			if (contentData.getMimetype() == null) {
				String cmName = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
				writer.guessMimetype(cmName);
			}
			if (contentData.getEncoding() == null) {
				writer.guessEncoding();
			}
			// Nécessaire, car non mis à jour après putContent
			nodeService.setProperty(nodeRef, qname, writer.getContentData());
		}
	}
	
	@Override
	public void delete(List<NodeReference> nodeReferences) {
		for (NodeReference nodeReference : nodeReferences) {
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
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}
}
