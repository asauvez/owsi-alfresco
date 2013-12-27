package fr.openwide.alfresco.repository.remote.node.web.script;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.node.service.NodeRemoteService.CHILDREN_NODE_SERVICE;
import fr.openwide.alfresco.repository.api.remote.exception.RepositoryRemoteException;

public class ChildrenNodeWebScript extends AbstractNodeWebScript<List<RepositoryNode>, CHILDREN_NODE_SERVICE> {

	@Override
	protected List<RepositoryNode> executeImpl(Resource content, CHILDREN_NODE_SERVICE parameter,  WebScriptRequest req, Status status, Cache cache) throws RepositoryRemoteException {
		return nodeService.getChildren(parameter.nodeReference, parameter.childAssocName, parameter.nodeFetchDetails);
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(CHILDREN_NODE_SERVICE.class);
	}

}
