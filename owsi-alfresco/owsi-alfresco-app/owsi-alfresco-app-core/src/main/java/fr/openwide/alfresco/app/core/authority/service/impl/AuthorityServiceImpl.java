package fr.openwide.alfresco.app.core.authority.service.impl;

import java.util.List;

import fr.openwide.alfresco.api.core.authority.exception.AuthorityExistsRemoteException;
import fr.openwide.alfresco.api.core.authority.model.RepositoryAuthorityQueryParameters;
import fr.openwide.alfresco.api.core.node.binding.content.NodeContentDeserializationParameters;
import fr.openwide.alfresco.api.core.node.binding.content.NodeContentSerializationParameters;
import fr.openwide.alfresco.api.core.node.exception.NoSuchNodeRemoteException;
import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.app.core.authority.service.AuthorityService;
import fr.openwide.alfresco.app.core.node.service.NodeService;

public class AuthorityServiceImpl implements AuthorityService {

	private final NodeService nodeService;

	public AuthorityServiceImpl(NodeService nodeService) {
		this.nodeService = nodeService;
	}
	
	@Override
	public RepositoryNode getUser(String userName, NodeScope nodeScope) throws NoSuchNodeRemoteException {
		GET_USER payload = new GET_USER();
		payload.userName = userName;
		payload.nodeScope = nodeScope;
		return nodeService.callNodeSerializer(payload, nodeScope);
	}
	
	@Override
	public RepositoryNode createUser(String userName, String firstName, String lastName, String email, String password, NodeScope nodeScope) throws AuthorityExistsRemoteException {
		CREATE_USER payload = new CREATE_USER();
		payload.userName = userName;
		payload.firstName = firstName;
		payload.lastName = lastName;
		payload.email = email;
		payload.password = password;
		payload.nodeScope = nodeScope;
		return nodeService.callNodeSerializer(payload, nodeScope);
	}
	
	@Override
	public void deleteUser(String userName) throws NoSuchNodeRemoteException {
		DELETE_USER payload = new DELETE_USER();
		payload.userName = userName;
		nodeService.callNodeUploadSerializer(payload, null, new NodeContentSerializationParameters(), new NodeContentDeserializationParameters());
	}
	
	@Override
	public void updateUserPassword(String userName, String newPassword) throws NoSuchNodeRemoteException {
		UPDATE_USER_PASSWORD payload = new UPDATE_USER_PASSWORD();
		payload.userName = userName;
		payload.newPassword = newPassword;
		nodeService.callNodeUploadSerializer(payload, null, new NodeContentSerializationParameters(), new NodeContentDeserializationParameters());
	}
	
	@Override
	public List<RepositoryNode> getContainedAuthorities(RepositoryAuthorityQueryParameters searchParameters) {
		GET_CONTAINED_AUTHORITIES payload = new GET_CONTAINED_AUTHORITIES();
		payload.searchParameters = searchParameters;
		return nodeService.callNodeListSerializer(payload, searchParameters.getNodeScope());
	}
	
	@Override
	public RepositoryNode getGroup(String groupShortName, NodeScope nodeScope) throws NoSuchNodeRemoteException {
		GET_GROUP payload = new GET_GROUP();
		payload.groupShortName = groupShortName;
		payload.nodeScope = nodeScope;
		return nodeService.callNodeSerializer(payload, nodeScope);
	}
	
	@Override
	public RepositoryNode createRootGroup(String groupShortName, String groupDisplayName, NodeScope nodeScope)
			throws AuthorityExistsRemoteException {
		CREATE_ROOT_GROUP payload = new CREATE_ROOT_GROUP();
		payload.groupShortName = groupShortName;
		payload.groupDisplayName = groupDisplayName;
		payload.nodeScope = nodeScope;
		return nodeService.callNodeSerializer(payload, nodeScope);
	}
	
	@Override
	public void deleteGroup(String groupShortName) throws NoSuchNodeRemoteException {
		DELETE_GROUP payload = new DELETE_GROUP();
		payload.groupShortName = groupShortName;
		nodeService.callNodeUploadSerializer(payload, null, new NodeContentSerializationParameters(), new NodeContentDeserializationParameters());
	}
	
	@Override
	public void addToGroup(String subAuthorityFullName, String parentGroupShortName) {
		ADD_TO_GROUP payload = new ADD_TO_GROUP();
		payload.subAuthorityFullName = subAuthorityFullName;
		payload.parentGroupShortName = parentGroupShortName;
		nodeService.callNodeUploadSerializer(payload, null, new NodeContentSerializationParameters(), new NodeContentDeserializationParameters());
	}
	
	@Override
	public void removeFromGroup(String subAuthorityFullName, String parentGroupShortName) {
		REMOVE_FROM_GROUP payload = new REMOVE_FROM_GROUP();
		payload.subAuthorityFullName = subAuthorityFullName;
		payload.parentGroupShortName = parentGroupShortName;
		nodeService.callNodeUploadSerializer(payload, null, new NodeContentSerializationParameters(), new NodeContentDeserializationParameters());
	}

}
