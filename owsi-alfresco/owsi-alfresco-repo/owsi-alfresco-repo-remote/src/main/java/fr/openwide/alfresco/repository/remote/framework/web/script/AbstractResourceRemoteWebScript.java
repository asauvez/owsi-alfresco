package fr.openwide.alfresco.repository.remote.framework.web.script;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import fr.openwide.alfresco.repository.api.remote.exception.RepositoryRemoteException;

/**
 * Base class for web-service which provides automatic two argument handling. First argument must be of type File 
 */
public abstract class AbstractResourceRemoteWebScript<R, P> extends AbstractParameterRemoteWebScript<R, P> {

	@Override
	protected R executeImpl(WebScriptRequest req, WebScriptResponse res, Status status, Cache cache) throws RepositoryRemoteException {
		P payload = extractPayload(req);
		
		Resource resource = new InputStreamResource(req.getContent().getInputStream());
		return executeImpl(resource, payload, status, cache);
	}

	@Override
	protected R executeImpl(P payload, Status status, Cache cache) throws RepositoryRemoteException {
		// Pas utilisé : On surcharge l'autre implémentation
		return null;
	}

	protected abstract R executeImpl(Resource content, P payload, Status status, Cache cache) throws RepositoryRemoteException;

}
