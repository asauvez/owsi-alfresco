package fr.openwide.alfresco.repository.core.node.web.script;

import org.springframework.core.io.Resource;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.node.service.NodeRemoteService.CREATE_NODE_SERVICE;
import fr.openwide.alfresco.repository.api.remote.exception.RepositoryRemoteException;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

public class CreateNodeWebScript extends AbstractNodeWebScript<NodeReference, RepositoryNode> {

	@Override
	protected NodeReference executeImpl(Resource content, RepositoryNode node, WebScriptRequest req, Status status, Cache cache)
			throws RepositoryRemoteException {
		String contentProperty = req.getHeader(CREATE_NODE_SERVICE.CONTENT_PROPERTY_HEADER);
		if (contentProperty != null) {
			node.getContentResources().put(NameReference.create(contentProperty), content);
		}
		return nodeService.create(node);
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(RepositoryNode.class);
	}

}
