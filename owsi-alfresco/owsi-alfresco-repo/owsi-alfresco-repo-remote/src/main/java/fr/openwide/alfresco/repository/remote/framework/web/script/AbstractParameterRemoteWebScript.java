package fr.openwide.alfresco.repository.remote.framework.web.script;

import java.io.IOException;

import org.springframework.extensions.webscripts.WebScriptRequest;

import fr.openwide.alfresco.repository.api.remote.exception.InvalidPayloadRemoteException;

/**
 * Base class for web services that provide automatic one argument handling. 
 * Payload must be a unique object in request parameter, serialized with Jackson and of type « P »
 */
public abstract class AbstractParameterRemoteWebScript<R, P> extends AbstractPayloadRemoteWebScript<R, P> {

	@Override
	protected String getRawPayload(WebScriptRequest req) throws IOException {
		return req.getHeader(InvalidPayloadRemoteException.HEADER_PAYLOAD);
	}

}
