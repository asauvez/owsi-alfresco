package fr.openwide.alfresco.repository.remote.framework.web.script;

import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import fr.openwide.alfresco.repository.api.remote.exception.RepositoryRemoteException;

/**
 * Base class for web-service which provides automatic two argument handling. First argument must be of type File 
 */
public abstract class AbstractInputStreamRemoteWebScript<R, P> extends AbstractParameterRemoteWebScript<R, P> {

	@Override
	protected R executeImpl(WebScriptRequest req, WebScriptResponse res, Status status, Cache cache) throws RepositoryRemoteException {
		P payload = extractPayload(req);
		return executeImpl(req.getContent(), payload, status, cache);
	}

	@Override
	protected R executeImpl(P payload, Status status, Cache cache) throws RepositoryRemoteException {
		// Pas utilisé : On surcharge l'autre implémentation
		return null;
	}

	protected abstract R executeImpl(Content content, P payload, Status status, Cache cache) throws RepositoryRemoteException;

}
