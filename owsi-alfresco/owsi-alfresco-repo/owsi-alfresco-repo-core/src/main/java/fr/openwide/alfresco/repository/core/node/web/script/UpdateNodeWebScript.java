package fr.openwide.alfresco.repository.core.node.web.script;

import org.springframework.core.io.Resource;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.repository.api.node.service.NodeRemoteService.CREATE_NODE_SERVICE;
import fr.openwide.alfresco.repository.api.node.service.NodeRemoteService.UPDATE_NODE_SERVICE;
import fr.openwide.alfresco.repository.api.remote.exception.RepositoryRemoteException;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class UpdateNodeWebScript extends AbstractNodeWebScript<Void, UPDATE_NODE_SERVICE> {

	@Override
	protected Void executeImpl(Resource content, UPDATE_NODE_SERVICE request, WebScriptRequest req, Status status, Cache cache) throws RepositoryRemoteException {
		String contentProperty = req.getHeader(CREATE_NODE_SERVICE.CONTENT_PROPERTY_HEADER);
		if (contentProperty != null) {
			request.node.getContentResources().put(NameReference.create(contentProperty), content);
		}
		nodeService.update(request.node, request.details);
		return null;
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(UPDATE_NODE_SERVICE.class);
	}

}
