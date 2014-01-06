package fr.openwide.alfresco.repository.core.node.web.script;

import java.util.List;
import java.util.Objects;

import org.springframework.core.io.Resource;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.node.service.NodeRemoteService.CHILDREN_NODE_SERVICE;

public class ChildrenNodeWebScript extends AbstractNodeWebScript<List<RepositoryNode>, CHILDREN_NODE_SERVICE> {

	@Override
	protected List<RepositoryNode> executeImpl(Resource content, CHILDREN_NODE_SERVICE parameter,  WebScriptRequest req, Status status, Cache cache) {
		return nodeService.getChildren(
				Objects.requireNonNull(parameter.nodeReference, "NodeReference"), 
				Objects.requireNonNull(parameter.childAssocTypeName, "ChildAssocTypeName"), 
				Objects.requireNonNull(parameter.nodeFetchDetails, "NodeFetchDetails"));
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(CHILDREN_NODE_SERVICE.class);
	}

}
