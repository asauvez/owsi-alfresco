package fr.openwide.alfresco.repository.remote.search.web.script;

import java.util.List;

import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.exception.RepositoryRemoteException;
import fr.openwide.alfresco.repository.api.search.service.NodeSearchRemoteService.CHILDREN_NODE_SERVICE;

public class ChildrenNodeWebScript extends AbstractNodeSearchWebScript<List<RepositoryNode>, CHILDREN_NODE_SERVICE> {

	@Override
	protected List<RepositoryNode> executeImpl(CHILDREN_NODE_SERVICE parameter, Status status, Cache cache) throws RepositoryRemoteException {
		return nodeSearchService.getChildren(parameter.nodeReference, parameter.childAssocName, parameter.nodeFetchDetails);
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(CHILDREN_NODE_SERVICE.class);
	}

}
