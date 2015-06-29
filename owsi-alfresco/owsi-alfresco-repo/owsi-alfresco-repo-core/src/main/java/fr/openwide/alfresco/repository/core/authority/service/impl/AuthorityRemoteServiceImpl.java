package fr.openwide.alfresco.repository.core.authority.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.PersonService;

import fr.openwide.alfresco.api.core.authority.model.RepositoryAuthoritySearchParameters;
import fr.openwide.alfresco.api.core.authority.service.AuthorityRemoteService;
import fr.openwide.alfresco.api.core.node.exception.NoSuchNodeRemoteException;
import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.node.service.NodeRemoteService;
import fr.openwide.alfresco.repository.remote.conversion.service.ConversionService;

public class AuthorityRemoteServiceImpl implements AuthorityRemoteService {

	private NodeRemoteService nodeRemoteService;
	private ConversionService conversionService;

	private PersonService personService;
	private AuthorityService authorityService;
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
	public List<RepositoryNode> getContainedUsers(RepositoryAuthoritySearchParameters searchParameters) {
		return getContained(AuthorityType.USER, searchParameters);
	}

	@Override
	public List<RepositoryNode> getContainedGroups(RepositoryAuthoritySearchParameters searchParameters) {
		return getContained(AuthorityType.GROUP, searchParameters);
	}

	private List<RepositoryNode> getContained(AuthorityType type, RepositoryAuthoritySearchParameters searchParameters) {
		Set<String> authorities = authorityService.getContainedAuthorities(type, 
				searchParameters.getParentAuthority().getName(), 
				searchParameters.isImmediate());
		
		Pattern pattern = (searchParameters.getFilterValue() != null) 
				? Pattern.compile(".*\\b" + searchParameters.getFilterValue().toLowerCase() + ".*") : null;
		
		List<RepositoryNode> nodes = new ArrayList<RepositoryNode>();
		for (String authority : authorities) {
			NodeRef nodeRef = authorityService.getAuthorityNodeRef(authority);
			
			if (pattern != null) {
				String nodeName = (String) nodeService.getProperty(nodeRef, 
						conversionService.getRequired(searchParameters.getFilterProperty()));
				if (! pattern.matcher(nodeName.toLowerCase()).matches()) {
					continue;
				}
			}
			
			nodes.add(nodeRemoteService.get(conversionService.get(nodeRef), searchParameters.getNodeScope()));
		}
		return nodes;
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
	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
}
