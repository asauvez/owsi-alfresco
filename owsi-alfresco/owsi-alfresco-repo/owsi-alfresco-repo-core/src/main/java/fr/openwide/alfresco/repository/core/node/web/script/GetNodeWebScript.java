package fr.openwide.alfresco.repository.core.node.web.script;

import org.springframework.core.io.Resource;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.node.service.NodeRemoteService.GET_NODE_SERVICE;
import fr.openwide.alfresco.repository.api.remote.exception.RepositoryRemoteException;

public class GetNodeWebScript extends AbstractNodeWebScript<RepositoryNode, GET_NODE_SERVICE> {

	@Override
	protected RepositoryNode executeImpl(Resource content, GET_NODE_SERVICE parameter,  WebScriptRequest req, Status status, Cache cache) throws RepositoryRemoteException {
		return nodeService.get(parameter.nodeReference, parameter.nodeFetchDetails);
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(GET_NODE_SERVICE.class);
	}

}
