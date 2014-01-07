package fr.openwide.alfresco.app.core.node.service;

import java.io.OutputStream;

import org.springframework.web.client.ResponseExtractor;

import fr.openwide.alfresco.repository.api.node.model.RepositoryContentData;
import fr.openwide.alfresco.repository.api.node.service.NodeRemoteService;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;
import fr.openwide.alfresco.repository.api.remote.model.endpoint.GetMethodEndpoint;

public interface NodeService extends NodeRemoteService {

	GetMethodEndpoint<Void> GET_NODE_CONTENT_ENDPOINT = new GetMethodEndpoint<Void>("/api/node/content{property}/{workspace}/{store}/{uuid}") {};
	
	void getNodeContent(NodeReference nodeReference, NameReference property, ResponseExtractor<?> responseExtractor);

	RepositoryContentData getNodeContent(NodeReference nodeReference, NameReference property, OutputStream out);

}
