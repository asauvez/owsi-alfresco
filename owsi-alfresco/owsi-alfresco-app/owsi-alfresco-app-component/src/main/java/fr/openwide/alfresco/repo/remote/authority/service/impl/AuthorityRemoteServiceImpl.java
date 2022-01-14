package fr.openwide.alfresco.repo.remote.authority.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import org.alfresco.model.ContentModel;
import org.alfresco.query.PagingRequest;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.MutableAuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyMap;

import fr.openwide.alfresco.api.core.authority.exception.AuthorityExistsRemoteException;
import fr.openwide.alfresco.api.core.authority.model.AuthorityReference;
import fr.openwide.alfresco.api.core.authority.model.AuthorityQueryParameters;
import fr.openwide.alfresco.api.core.authority.service.AuthorityRemoteService;
import fr.openwide.alfresco.api.core.node.exception.NoSuchNodeRemoteException;
import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.node.service.NodeRemoteService;
import fr.openwide.alfresco.api.core.remote.exception.RepositoryRemoteException;
import fr.openwide.alfresco.api.core.search.model.SortDefinition;
import fr.openwide.alfresco.repo.remote.conversion.service.ConversionService;

public class AuthorityRemoteServiceImpl implements AuthorityRemoteService {

	private NodeRemoteService nodeRemoteService;
	private ConversionService conversionService;

	private PersonService personService;
	private AuthorityService authorityService;
	private MutableAuthenticationService authenticationService;
	private NodeService nodeService;

	@Override
	public RepositoryNode getUser(String userName, NodeScope nodeScope) throws NoSuchNodeRemoteException {
		NodeRef nodeRef = personService.getPersonOrNull(userName);
		if (nodeRef == null) {
			throw new NoSuchNodeRemoteException(userName);
		}
		return nodeRemoteService.get(conversionService.get(nodeRef), nodeScope);
	}

	@Override
	public RepositoryNode createUser(String userName, String firstName, String lastName, String email, String password,
			NodeScope nodeScope) throws AuthorityExistsRemoteException {
		if (personService.personExists(userName)) {
			throw new AuthorityExistsRemoteException();
		}
		PropertyMap properties = new PropertyMap();
		properties.put(ContentModel.PROP_USERNAME, userName);
		properties.put(ContentModel.PROP_FIRSTNAME, firstName);
		properties.put(ContentModel.PROP_LASTNAME, lastName);
		properties.put(ContentModel.PROP_EMAIL, email);
		try {
			NodeRef personRef = personService.createPerson(properties);
			authenticationService.createAuthentication(userName, password.toCharArray());
			authenticationService.setAuthenticationEnabled(userName, true);
			return nodeRemoteService.get(conversionService.get(personRef), nodeScope);
		} catch (Exception e) {
			throw new RepositoryRemoteException(e);
		}
	}

	@Override
	public void deleteUser(String userName) throws NoSuchNodeRemoteException {
		if ( !personService.personExists(userName)) {
			throw new NoSuchNodeRemoteException(userName);
		}
		try {
			personService.deletePerson(userName);
		} catch (Exception e) {
			throw new RepositoryRemoteException(e);
		}
	}

	@Override
	public void updateUserPassword(String userName, String newPassword) throws NoSuchNodeRemoteException {
		if ( !personService.personExists(userName)) {
			throw new NoSuchNodeRemoteException(userName);
		}
		try {
			authenticationService.setAuthentication(userName, newPassword.toCharArray());
		} catch (Exception e) {
			throw new RepositoryRemoteException(e);
		}
	}

	@Override
	public List<RepositoryNode> getContainedAuthorities(AuthorityQueryParameters searchParameters, NodeScope nodeScope) {
		AuthorityType authorityType = (searchParameters.getAuthorityType() != null) ? AuthorityType.valueOf(searchParameters.getAuthorityType().name()) : null;
		
		Collection<String> authorities;
		if (AuthorityReference.GROUP_EVERYONE.equals(searchParameters.getParentAuthority())) {
			String zone = (searchParameters.getZone() != null) ? searchParameters.getZone().getName() : null;
			authorities = authorityService.getAuthorities(authorityType, zone, null, 
					false, false, new PagingRequest(0, Integer.MAX_VALUE, null)).getPage();
		} else {
			if (searchParameters.getZone() != null) {
				throw new IllegalArgumentException("Zone filter is only supported with GROUP_EVERYONE authoriry.");
			}
			
			authorities = authorityService.getContainedAuthorities(authorityType, 
					searchParameters.getParentAuthority().getName(), 
					searchParameters.isImmediate());
			
			if (searchParameters.isIncludingParent()) {
				authorities.add(searchParameters.getParentAuthority().getName());
			}
		}
		
		Pattern pattern = (searchParameters.getFilterValue() != null) 
				? Pattern.compile(".*\\b" + searchParameters.getFilterValue().toLowerCase() + ".*") : null;
		
		List<RepositoryNode> nodes = new ArrayList<RepositoryNode>();
		for (String authority : authorities) {
			NodeRef nodeRef = authorityService.getAuthorityNodeRef(authority);
			
			if (pattern != null) {
				Object nodeName = nodeService.getProperty(nodeRef, 
						conversionService.getRequired(searchParameters.getFilterProperty()));
				if (nodeName == null || ! pattern.matcher(nodeName.toString().toLowerCase()).matches()) {
					continue;
				}
			}
			
			nodes.add(nodeRemoteService.get(conversionService.get(nodeRef), nodeScope));
		}
		
		sortNodes(nodes, searchParameters.getSorts());
		
		return nodes;
	}

	private void sortNodes(List<RepositoryNode> nodes, final List<SortDefinition> sorts) {
		if (! sorts.isEmpty()) {
			Collections.sort(nodes, new Comparator<RepositoryNode>() {
				@Override
				public int compare(RepositoryNode o1, RepositoryNode o2) {
					for (SortDefinition sort : sorts) {
						QName property = conversionService.getRequired(sort.getProperty());
						int factor = sort.isAscending() ? 1 : -1;
						Serializable value1 = nodeService.getProperty(conversionService.getRequired(o1.getNodeReference()), property);
						Serializable value2 = nodeService.getProperty(conversionService.getRequired(o2.getNodeReference()), property);
						
						if (value1 instanceof String && value2 instanceof String) {
							int diff = ((String) value1).compareToIgnoreCase((String) value2);
							if (diff != 0) {
								return factor * diff;
							}
						} else if (value1 instanceof Comparable && value2 instanceof Comparable) {
							@SuppressWarnings("unchecked")
							int diff = ((Comparable<Object>) value1).compareTo(value2);
							if (diff != 0) {
								return factor * diff;
							}
						} else if (value1 != null) {
							return factor * -1;
						} else if (value2 != null) {
							return factor;
						}
					}
					return 0;
				}
			});
		}
	}

	@Override
	public RepositoryNode getGroup(String groupShortName, NodeScope nodeScope) throws NoSuchNodeRemoteException {
		NodeRef groupNodeRef = authorityService.getAuthorityNodeRef(authorityService.getName(AuthorityType.GROUP, groupShortName));
		if (groupNodeRef != null) {
			return nodeRemoteService.get(conversionService.get(groupNodeRef), nodeScope);
		} else {
			throw new NoSuchNodeRemoteException(groupShortName);
		}
	}

	@Override
	public RepositoryNode createRootGroup(String groupShortName, String groupDisplayName, NodeScope nodeScope)
			throws AuthorityExistsRemoteException {
		String groupFullName = authorityService.getName(AuthorityType.GROUP, groupShortName);
		if (authorityService.authorityExists(groupFullName)) {
			throw new AuthorityExistsRemoteException();
		}
		try {
			authorityService.createAuthority(AuthorityType.GROUP, groupShortName, groupDisplayName, authorityService.getDefaultZones());
			return getGroup(groupShortName, nodeScope);
		} catch (Exception e) {
			throw new RepositoryRemoteException(e);
		}
	}

	@Override
	public void deleteGroup(String groupShortName) throws NoSuchNodeRemoteException {
		String groupFullName = authorityService.getName(AuthorityType.GROUP, groupShortName);
		if (! authorityService.authorityExists(groupFullName)) {
			throw new NoSuchNodeRemoteException(groupFullName);
		}
		try {
			authorityService.deleteAuthority(groupFullName);
		} catch (Exception e) {
			throw new RepositoryRemoteException(e);
		}
	}

	@Override
	public void addToGroup(String subAuthorityFullName, String parentGroupShortName) throws NoSuchNodeRemoteException {
		String parentGroupFullName = authorityService.getName(AuthorityType.GROUP, parentGroupShortName);
		if (! authorityService.authorityExists(parentGroupFullName)) {
			throw new NoSuchNodeRemoteException(parentGroupFullName);
		}
		if (! authorityService.authorityExists(subAuthorityFullName)) {
			throw new NoSuchNodeRemoteException(subAuthorityFullName);
		}
		try {
			authorityService.addAuthority(parentGroupFullName, subAuthorityFullName);
		} catch (Exception e) {
			throw new RepositoryRemoteException(e);
		}
	}

	@Override
	public void removeFromGroup(String subAuthorityFullName, String parentGroupShortName) throws NoSuchNodeRemoteException {
		String parentGroupFullName = authorityService.getName(AuthorityType.GROUP, parentGroupShortName);
		if (! authorityService.authorityExists(parentGroupFullName)) {
			throw new NoSuchNodeRemoteException(parentGroupFullName);
		}
		if (! authorityService.authorityExists(subAuthorityFullName)) {
			throw new NoSuchNodeRemoteException(subAuthorityFullName);
		}
		try {
			authorityService.removeAuthority(parentGroupFullName, subAuthorityFullName);
		} catch (Exception e) {
			throw new RepositoryRemoteException(e);
		}
	}

	public void setNodeRemoteService(NodeRemoteService nodeRemoteService) {
		this.nodeRemoteService = nodeRemoteService;
	}
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public void setAuthorityService(AuthorityService authorityService) {
		this.authorityService = authorityService;
	}
	public void setAuthenticationService(MutableAuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}
	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
}
