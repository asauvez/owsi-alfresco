package fr.openwide.alfresco.repository.remote.search.web.script;

import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.exception.RepositoryRemoteException;
import fr.openwide.alfresco.repository.api.search.service.NodeSearchRemoteService.GET_NODE_SERVICE;

public class GetNodeWebScript extends AbstractNodeSearchWebScript<RepositoryNode, GET_NODE_SERVICE> {

	@Override
	protected RepositoryNode executeImpl(GET_NODE_SERVICE parameter, Status status, Cache cache) throws RepositoryRemoteException {
		return nodeSearchService.get(parameter.nodeReference, parameter.nodeFetchDetails);
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(GET_NODE_SERVICE.class);
	}

}
