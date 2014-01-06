package fr.openwide.alfresco.repository.core.node.service.impl;

import java.io.IOException;
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
import org.alfresco.repo.security.permissions.AccessDeniedException;
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
import org.springframework.core.io.Resource;

import fr.openwide.alfresco.repository.api.node.exception.DuplicateChildNameException;
import fr.openwide.alfresco.repository.api.node.exception.NoSuchNodeException;
import fr.openwide.alfresco.repository.api.node.model.NodeFetchDetails;
import fr.openwide.alfresco.repository.api.node.model.RepositoryAuthority;
import fr.openwide.alfresco.repository.api.node.model.RepositoryAuthorityPermission;
import fr.openwide.alfresco.repository.api.node.model.RepositoryChildAssociation;
import fr.openwide.alfresco.repository.api.node.model.RepositoryContentData;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.node.model.RepositoryPermission;
import fr.openwide.alfresco.repository.api.node.service.NodeRemoteService;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;
import fr.openwide.alfresco.repository.core.remote.service.ConversionService;
import fr.openwide.alfresco.repository.remote.framework.exception.InvalidPayloadException;

public class NodeRemoteServiceImpl implements NodeRemoteService {

	private NodeService nodeService;
	private ContentService contentService;
	private PermissionService permissionService;

	private ConversionService conversionService;

	@Override
	public RepositoryNode get(NodeReference nodeReference, NodeFetchDetails details) throws NoSuchNodeException {
		return getRepositoryNode(conversionService.getRequired(nodeReference), details);
	}

	@Override
	public List<RepositoryNode> getChildren(
			NodeReference nodeReference, NameReference childAssocTypeName, NodeFetchDetails details) {
		List<ChildAssociationRef> assocs = nodeService.getChildAssocs(
				conversionService.getRequired(nodeReference), 
				conversionService.getRequired(childAssocTypeName), 
				RegexQNamePattern.MATCH_ALL, true);
		List<RepositoryNode> res = new ArrayList<>();
		for (ChildAssociationRef assoc : assocs) {
			res.add(getRepositoryNode(assoc.getChildRef(), details));
		}
		return res;
	}

	@Override
	public List<RepositoryNode> getTargetAssocs(
			NodeReference nodeReference, NameReference assocName, NodeFetchDetails details) {
		List<AssociationRef> assocs = nodeService.getTargetAssocs(
				conversionService.getRequired(nodeReference), 
				conversionService.getRequired(assocName));
		List<RepositoryNode> res = new ArrayList<>();
		for (AssociationRef assoc : assocs) {
			res.add(getRepositoryNode(assoc.getTargetRef(), details));
		}
		return res;
	}

	@Override
	public List<RepositoryNode> getSourceAssocs(
			NodeReference nodeReference, NameReference assocName, NodeFetchDetails details) {
		List<AssociationRef> assocs = nodeService.getSourceAssocs(
				conversionService.getRequired(nodeReference), 
				conversionService.getRequired(assocName));
		List<RepositoryNode> res = new ArrayList<>();
		for (AssociationRef assoc : assocs) {
			res.add(getRepositoryNode(assoc.getSourceRef(), details));
		}
		return res;
	}

	private RepositoryNode getRepositoryNode(NodeRef nodeRef, NodeFetchDetails details) throws NoSuchNodeException {
		NodeReference nodeReference = conversionService.get(nodeRef);
		if (! nodeService.exists(nodeRef)) {
			throw new NoSuchNodeException(nodeReference.getReference());
		}
		RepositoryNode node = new RepositoryNode();
		if (details.isNodeReference()) {
			node.setNodeReference(conversionService.get(nodeRef));
		}
		if (details.isType()) {
			node.setType(conversionService.get(nodeService.getType(nodeRef)));
		}
		if (details.getPrimaryParent() != null) {
			ChildAssociationRef primaryParent = nodeService.getPrimaryParent(nodeRef);
			if (primaryParent.getParentRef() != null) {
				node.setPrimaryParentAssociation(new RepositoryChildAssociation(
						getRepositoryNode(primaryParent.getParentRef(), details.getPrimaryParent()),
						conversionService.get(primaryParent.getTypeQName())));
			}
		}
		for (NameReference property : details.getProperties()) {
			Serializable value = nodeService.getProperty(nodeRef, conversionService.getRequired(property));
			if (value != null) {
				node.getProperties().put(property, conversionService.getForApplication(value));
			}
		}
		for (NameReference property : details.getContentStrings()) {
			ContentReader reader = contentService.getReader(nodeRef, conversionService.getRequired(property));
			if (reader != null) {
				String content = reader.getContentString();
				node.getContentStrings().put(property, content);
			}
		}
		for (NameReference aspect : details.getAspects()) {
			if (nodeService.hasAspect(nodeRef, conversionService.getRequired(aspect))) {
				node.getAspects().add(aspect);
			}
		}
		// get associations
		for (Entry<NameReference, NodeFetchDetails> entry : details.getChildAssociations().entrySet()) {
			node.getChildAssociations().put(
					entry.getKey(), 
					getChildren(nodeReference, entry.getKey(), entry.getValue()));
		}
		for (Entry<NameReference, NodeFetchDetails> entry : details.getTargetAssocs().entrySet()) {
			node.getTargetAssocs().put(
					entry.getKey(), 
					getTargetAssocs(nodeReference, entry.getKey(), entry.getValue()));
		}
		for (Entry<NameReference, NodeFetchDetails> entry : details.getSourceAssocs().entrySet()) {
			node.getSourceAssocs().put(
					entry.getKey(), 
					getSourceAssocs(nodeReference, entry.getKey(), entry.getValue()));
		}
		// get premissions
		for (RepositoryPermission permission : details.getUserPermissions()) {
			if (permissionService.hasPermission(nodeRef, permission.getName()) == AccessStatus.ALLOWED) {
				node.getUserPermissions().add(permission);
			}
		}
		if (details.isAccessPermissions()) {
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

	@Override
	public NodeReference create(RepositoryNode node) throws DuplicateChildNameException {
		if (node.getNodeReference() != null) {
			throw new InvalidPayloadException("Node already has a reference");
		}
		String cmName = (String) node.getProperties().get(conversionService.get(ContentModel.PROP_NAME));
		if (cmName == null) {
			throw new InvalidPayloadException("Property is required: " + conversionService.get(ContentModel.PROP_NAME));
		}
		Map<QName, Serializable> properties = new LinkedHashMap<QName, Serializable>();
		for (Entry<NameReference, Serializable> property : node.getProperties().entrySet()) {
			Serializable value = property.getValue();
			if (! (value instanceof RepositoryContentData)) {
				properties.put(
					conversionService.getRequired(property.getKey()), 
					conversionService.getForRepository(value));
			}
		}
		try {
			RepositoryChildAssociation primaryParent = node.getPrimaryParentAssociation();
			NodeRef nodeRef = nodeService.createNode(
					conversionService.getRequired(primaryParent.getParentNode().getNodeReference()), 
					conversionService.getRequired(primaryParent.getType()), 
					QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, cmName.toLowerCase()), 
					conversionService.getRequired(node.getType()), 
					properties).getChildRef();
			
			for (NameReference aspectName : node.getAspects()) {
				nodeService.addAspect(nodeRef, conversionService.getRequired(aspectName), null);
			}
			setContents(nodeRef, node);
			setPermissions(nodeRef, node);
			return conversionService.get(nodeRef);
		} catch (DuplicateChildNodeNameException e) {
			throw new DuplicateChildNameException(cmName, e);
		}
	}

	@Override
	public void update(RepositoryNode node, NodeFetchDetails details) throws DuplicateChildNameException {
		String cmName = (String) node.getProperties().get(conversionService.get(ContentModel.PROP_NAME));
		try {
			NodeRef nodeRef = conversionService.getRequired(node.getNodeReference());
			if (details.isType()) {
				nodeService.setType(nodeRef, conversionService.getRequired(node.getType()));
			}
			if (details.getPrimaryParent() != null) {
				if (cmName == null) {
					cmName = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
				}
				RepositoryChildAssociation repoPrimaryParent = node.getPrimaryParentAssociation();
				nodeService.moveNode(nodeRef, 
						conversionService.getRequired(repoPrimaryParent.getParentNode().getNodeReference()), 
						conversionService.getRequired(repoPrimaryParent.getType()), 
						QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, cmName.toLowerCase()));
			}
			for (NameReference propertyName : details.getProperties()) {
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
			for (NameReference aspectName : details.getAspects()) {
				if (node.getAspects().contains(aspectName)) {
					nodeService.addAspect(nodeRef, conversionService.getRequired(aspectName), null);
				} else {
					nodeService.removeAspect(nodeRef, conversionService.getRequired(aspectName));
				}
			}
			for (Entry<NameReference, NodeFetchDetails> entry : details.getChildAssociations().entrySet()) {
				for (RepositoryNode childNode : node.getChildAssociations().get(entry.getKey())) {
					childNode.setPrimaryParentAssociation(new RepositoryChildAssociation(node, entry.getKey()));
					saveOrUpdate(childNode, entry.getValue());
				}
			}
			setContents(nodeRef, node);
			if (details.isAccessPermissions()) {
				setPermissions(nodeRef, node);
			}
		} catch (DuplicateChildNodeNameException e) {
			throw new DuplicateChildNameException(cmName, e);
		}
	}

	private void saveOrUpdate(RepositoryNode node, NodeFetchDetails details) throws DuplicateChildNameException {
		if (node.getNodeReference() == null) {
			create(node);
		} else {
			update(node, details);
		}
	}
	
	private void setPermissions(final NodeRef nodeRef, final RepositoryNode node) {
		// Check si on a les droits
		if (permissionService.hasPermission(nodeRef, PermissionService.CHANGE_PERMISSIONS) != AccessStatus.ALLOWED) {
			throw new AccessDeniedException("You don't have the permission to change the permissions on " + nodeRef);
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

	private void setContents(NodeRef nodeRef, RepositoryNode node) {
		for (Entry<NameReference, String> entry : node.getContentStrings().entrySet()) {
			RepositoryContentData contentData = (RepositoryContentData) node.getProperties().get(entry.getKey());
			setContent(nodeRef, entry.getKey(), 
					(contentData != null) ? contentData : new RepositoryContentData(), 
					null, entry.getValue());
		}
		for (Entry<NameReference, Resource> entry : node.getContentResources().entrySet()) {
			RepositoryContentData contentData = (RepositoryContentData) node.getProperties().get(entry.getKey());
			setContent(nodeRef, entry.getKey(), 
					(contentData != null) ? contentData : new RepositoryContentData(), 
					entry.getValue(), null);
		}
	}

	private void setContent(NodeRef nodeRef, NameReference propertyName, RepositoryContentData contentData, 
			Resource contentResource, String contentString) {
		QName qname = conversionService.getRequired(propertyName);
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
		if (contentString != null) {
			writer.putContent(contentString);
		} else {
			try {
				writer.putContent(contentResource.getInputStream());
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
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
			nodeService.setProperty(nodeRef, qname, writer.getContentData());
		}
	}

	@Override
	public void delete(NodeReference nodeReference) {
		nodeService.deleteNode(conversionService.getRequired(nodeReference));
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
