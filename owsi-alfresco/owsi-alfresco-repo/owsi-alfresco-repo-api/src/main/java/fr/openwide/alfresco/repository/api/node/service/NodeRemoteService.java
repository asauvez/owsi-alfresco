package fr.openwide.alfresco.repository.api.node.service;

import java.io.InputStream;

import org.springframework.http.HttpMethod;

import fr.openwide.alfresco.repository.api.node.exception.DuplicateChildNameException;
import fr.openwide.alfresco.repository.api.node.model.NodeFetchDetails;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

public interface NodeRemoteService {

	interface CREATE_NODE_SERVICE {
		String URL = "/owsi/node";
		HttpMethod METHOD = HttpMethod.POST;
	}
	NodeReference create(RepositoryNode node, InputStream content) throws DuplicateChildNameException;

	class UPDATE_NODE_SERVICE {
		public static final String URL = "/owsi/node";
		public static final HttpMethod METHOD = HttpMethod.PUT;
		public RepositoryNode node;
		public NodeFetchDetails details;
	}
	void update(RepositoryNode node, NodeFetchDetails details, InputStream content);

	interface DELETE_NODE_SERVICE {
		String URL = "/owsi/node";
		HttpMethod METHOD = HttpMethod.DELETE;
	}
	void delete(NodeReference nodeReference);

}
