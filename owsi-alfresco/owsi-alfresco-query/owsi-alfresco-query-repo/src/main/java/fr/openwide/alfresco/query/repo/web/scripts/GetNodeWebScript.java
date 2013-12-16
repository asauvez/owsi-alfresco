package fr.openwide.alfresco.query.repo.web.scripts;

import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.query.api.search.model.NodeResult;
import fr.openwide.alfresco.query.api.search.service.NodeSearchRemoteService.GET_NODE_SERVICE;
import fr.openwide.alfresco.repository.api.remote.model.RepositoryRemoteException;

public class GetNodeWebScript extends AbstractNodeSearchWebScript<NodeResult, GET_NODE_SERVICE> {

	@Override
	protected NodeResult executeImpl(GET_NODE_SERVICE parameter, Status status, Cache cache) throws RepositoryRemoteException {
		return nodeSearchService.get(parameter.nodeReference, parameter.nodeFetchDetails);
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(GET_NODE_SERVICE.class);
	}

}
