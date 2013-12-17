package fr.openwide.alfresco.repository.remote.node.web.script;

import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.repository.api.node.service.NodeRemoteService.UPDATE_NODE_SERVICE;
import fr.openwide.alfresco.repository.api.remote.exception.RepositoryRemoteException;

public class UpdateNodeWebScript extends AbstractNodeWebScript<Void, UPDATE_NODE_SERVICE> {

	@Override
	protected Void executeImpl(Content content, UPDATE_NODE_SERVICE request, Status status, Cache cache) throws RepositoryRemoteException {
		nodeService.update(request.node, request.details, content.getInputStream());
		return null;
	}

	@Override
	protected Void executeImpl(UPDATE_NODE_SERVICE request, Status status, Cache cache) throws RepositoryRemoteException {
		// Pas utilisé : On surcharge l'autre implémentation
		return null;
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(UPDATE_NODE_SERVICE.class);
	}

}
