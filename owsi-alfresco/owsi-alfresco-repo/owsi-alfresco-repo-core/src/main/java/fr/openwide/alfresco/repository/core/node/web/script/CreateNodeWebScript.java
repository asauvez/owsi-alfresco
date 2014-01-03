package fr.openwide.alfresco.repository.core.node.web.script;

import org.springframework.core.io.Resource;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.repository.api.node.service.NodeRemoteService.CREATE_NODE_SERVICE;
import fr.openwide.alfresco.repository.api.remote.exception.RepositoryRemoteException;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

public class CreateNodeWebScript extends AbstractNodeWebScript<NodeReference, CREATE_NODE_SERVICE> {

	@Override
	protected NodeReference executeImpl(Resource content, CREATE_NODE_SERVICE request, WebScriptRequest req, Status status, Cache cache)
			throws RepositoryRemoteException {
		if (request.contentBodyProperty != null) {
			request.node.getContentResources().put(NameReference.create(request.contentBodyProperty), content);
		}
		return nodeService.create(request.node);
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(CREATE_NODE_SERVICE.class);
	}

}
