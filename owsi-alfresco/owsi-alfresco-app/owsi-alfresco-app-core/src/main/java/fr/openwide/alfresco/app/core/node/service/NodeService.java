package fr.openwide.alfresco.app.core.node.service;

import java.io.OutputStream;

import org.springframework.http.HttpMethod;
import org.springframework.web.client.ResponseExtractor;

import fr.openwide.alfresco.repository.api.node.model.RepositoryContentData;
import fr.openwide.alfresco.repository.api.node.service.NodeRemoteService;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

public interface NodeService extends NodeRemoteService {

	interface GET_NODE_CONTENT_SERVICE {
		String URL = "/api/node/content{property}/{workspace}/{store}/{uuid}";
		HttpMethod METHOD = HttpMethod.GET;
	}
	void getNodeContent(NodeReference nodeReference, NameReference property, ResponseExtractor<?> responseExtractor);

	RepositoryContentData getNodeContent(NodeReference nodeReference, NameReference property, OutputStream out);

}
