package fr.openwide.alfresco.query.repo.web.scripts;

import java.util.List;

import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.query.api.search.model.NodeResult;
import fr.openwide.alfresco.query.api.search.service.NodeSearchRemoteService.CHILDREN_NODE_SERVICE;
import fr.openwide.alfresco.repository.api.remote.model.RepositoryRemoteException;

public class ChildrenNodeWebScript extends AbstractNodeSearchWebScript<List<NodeResult>, CHILDREN_NODE_SERVICE> {

	@Override
	protected List<NodeResult> executeImpl(CHILDREN_NODE_SERVICE parameter, Status status, Cache cache) throws RepositoryRemoteException {
		return nodeSearchService.getChildren(parameter.nodeReference, parameter.childAssocName, parameter.nodeFetchDetails);
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(CHILDREN_NODE_SERVICE.class);
	}

}
