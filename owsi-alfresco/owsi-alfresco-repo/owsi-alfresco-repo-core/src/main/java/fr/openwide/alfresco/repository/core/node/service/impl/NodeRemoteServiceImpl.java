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
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.springframework.core.io.Resource;

import fr.openwide.alfresco.repository.api.node.exception.DuplicateChildNameException;
import fr.openwide.alfresco.repository.api.node.model.NodeFetchDetails;
import fr.openwide.alfresco.repository.api.node.model.RepositoryAuthority;
import fr.openwide.alfresco.repository.api.node.model.RepositoryAuthorityPermission;
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
	private AuthenticationService authenticationService;
	private AuthorityService authorityService;

	private ConversionService conversionService;

	@Override
	public RepositoryNode get(NodeReference nodeReference, NodeFetchDetails details) {
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

	public RepositoryNode getRepositoryNode(NodeRef nodeRef, NodeFetchDetails details) {
		NodeReference nodeReference = conversionService.get(nodeRef);
		RepositoryNode node = new RepositoryNode();
		if (details.isNodeReference()) {
			node.setNodeReference(conversionService.get(nodeRef));
		}
		if (details.isType()) {
			node.setType(conversionService.get(nodeService.getType(nodeRef)));
		}
		if (details.getPrimaryParent() != null) {
			ChildAssociationRef primaryParent = nodeService.getPrimaryParent(nodeRef);
			node.setPrimaryParent(getRepositoryNode(primaryParent.getParentRef(), details.getPrimaryParent()));
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
			NodeRef nodeRef = nodeService.createNode(
					conversionService.getRequired(node.getPrimaryParent().getNodeReference()), 
					ContentModel.ASSOC_CONTAINS, 
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
			throw new DuplicateChildNameException(e);
		}
	}

	@Override
	public void update(RepositoryNode node, NodeFetchDetails details) {
		NodeRef nodeRef = conversionService.getRequired(node.getNodeReference());
		if (details.isType()) {
			nodeService.setType(nodeRef, conversionService.getRequired(node.getType()));
		}
		if (details.getPrimaryParent() != null) {
			String cmName = (String) node.getProperties().get(conversionService.get(ContentModel.PROP_NAME));
			if (cmName == null) {
				throw new InvalidPayloadException("Property is required: " + conversionService.get(ContentModel.PROP_NAME));
			}
			NodeReference parentRef = node.getPrimaryParent().getNodeReference();
			ChildAssociationRef primaryParent = nodeService.getPrimaryParent(nodeRef);
			nodeService.moveNode(nodeRef, conversionService.getRequired(parentRef), primaryParent.getTypeQName(), 
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
				childNode.setPrimaryParent(node);
				try {
					saveOrUpdate(childNode, entry.getValue());
				} catch (DuplicateChildNameException e) {
					throw new InvalidPayloadException(e);
				}
			}
		}
		setContents(nodeRef, node);
		if (details.isAccessPermissions()) {
			setPermissions(nodeRef, node);
		}
	}

	private void saveOrUpdate(RepositoryNode node, NodeFetchDetails details) throws DuplicateChildNameException {
		if (node.getNodeReference() == null) {
			create(node);
		} else {
			update(node, details);
		}
	}

	private void setPermissions(NodeRef nodeRef, RepositoryNode node) {
		Set<RepositoryAuthorityPermission> newPermissions = node.getAccessPermissions();
		Set<RepositoryAuthorityPermission> oldPermissions = new HashSet<RepositoryAuthorityPermission>();
		for (AccessPermission oldPermission : permissionService.getAllSetPermissions(nodeRef)) {
			if (oldPermission.isSetDirectly()) {
				oldPermissions.add(new RepositoryAuthorityPermission(
						new RepositoryAuthority(oldPermission.getAuthority()),
						new RepositoryPermission(oldPermission.getPermission()),
						oldPermission.getAccessStatus() == AccessStatus.ALLOWED));
			}
		}
		
		final String userName = authenticationService.getCurrentUserName();
		Set<String> userAuthorities = AuthenticationUtil.runAs(new RunAsWork<Set<String>>() {
			@Override
			public Set<String> doWork() throws Exception {
				return authorityService.getAuthoritiesForUser(userName);
			}
		}, AuthenticationUtil.getSystemUserName());
		
		// ajout des nouvelles permissions
		for (RepositoryAuthorityPermission newPermission : newPermissions) {
			if (! oldPermissions.contains(newPermission)) {
				permissionService.setPermission(nodeRef, 
						newPermission.getAuthority().getName(), 
						newPermission.getPermission().getName(), 
						newPermission.isAllowed());
			}
		}
		
		// parcours des anciennes permissions à enlever qui ne concerne pas l'utilisateur connecté
		List<RepositoryAuthorityPermission> remainingPermissions = new ArrayList<RepositoryAuthorityPermission>();
		for (RepositoryAuthorityPermission oldPermission : oldPermissions) {
			if (! newPermissions.contains(oldPermission)) {
				if (! userAuthorities.contains(oldPermission.getAuthority())) {
					permissionService.deletePermission(nodeRef, oldPermission.getAuthority().getName(), oldPermission.getPermission().getName());
				} else {
					// stocke temporairement
					remainingPermissions.add(oldPermission);
				}
			}
		}
		
		// suppression des anciennes permissions en trop pour l'utilisateur connecté
		// En premier permissions non admin, histoire que l'on est encore les droits de supprimer les autres
		for (RepositoryAuthorityPermission oldPermission : remainingPermissions) {
			if (! isPermissionControl(oldPermission.getPermission().getName())) {
				permissionService.deletePermission(nodeRef, oldPermission.getAuthority().getName(), oldPermission.getPermission().getName());
			}
		}
		// En deuxième, permissions admin
		for (RepositoryAuthorityPermission oldPermission : remainingPermissions) {
			if (isPermissionControl(oldPermission.getPermission().getName())) {
				permissionService.deletePermission(nodeRef, oldPermission.getAuthority().getName(), oldPermission.getPermission().getName());
			}
		}
	}

	private boolean isPermissionControl(String permission) {
		return PermissionService.COORDINATOR.equals(permission)
				|| PermissionService.CHANGE_PERMISSIONS.equals(permission)
				|| PermissionService.FULL_CONTROL.equals(permission)
				|| PermissionService.ALL_PERMISSIONS.equals(permission);
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
	public void setAuthenticationService(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}
	public void setAuthorityService(AuthorityService authorityService) {
		this.authorityService = authorityService;
	}

}
