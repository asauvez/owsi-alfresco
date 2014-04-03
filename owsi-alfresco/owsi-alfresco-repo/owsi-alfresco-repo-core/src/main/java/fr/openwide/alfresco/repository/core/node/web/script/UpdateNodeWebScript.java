package fr.openwide.alfresco.repository.core.node.web.script;

import java.util.Objects;

import org.springframework.core.io.Resource;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.repository.api.node.service.NodeRemoteService.UPDATE_NODE_SERVICE;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class UpdateNodeWebScript extends AbstractNodeWebScript<Void, UPDATE_NODE_SERVICE> {

	@Override
	protected Void executeImpl(Resource content, UPDATE_NODE_SERVICE request, WebScriptRequest req, Status status, Cache cache) {
		if (request.contentBodyProperty != null) {
			request.node.getContentResources().put(NameReference.create(request.contentBodyProperty), content);
		}
		nodeService.update(
				Objects.requireNonNull(request.node, "RepositoryNode"), 
				Objects.requireNonNull(request.nodeScope, "NodeScope"));
		return null;
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(UPDATE_NODE_SERVICE.class);
	}

}
