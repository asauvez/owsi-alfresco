package fr.openwide.alfresco.repository.remote.framework.web.script;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * Base class for web-service which provides automatic two argument handling. First argument must be of type Resource
 */
public abstract class AbstractResourceRemoteWebScript<R, P> extends AbstractParameterRemoteWebScript<R, P> {

	@Override
	protected R executeImpl(WebScriptRequest req, WebScriptResponse res, Status status, Cache cache) {
		P payload = extractPayload(req);
		
		Resource resource = new InputStreamResource(req.getContent().getInputStream());
		return executeImpl(resource, payload, req, status, cache);
	}

	@Override
	protected R executeImpl(P payload, Status status, Cache cache) {
		// Pas utilisé : On surcharge l'autre implémentation
		throw new UnsupportedOperationException();
	}

	protected abstract R executeImpl(Resource content, P payload, WebScriptRequest req, Status status, Cache cache);

}
