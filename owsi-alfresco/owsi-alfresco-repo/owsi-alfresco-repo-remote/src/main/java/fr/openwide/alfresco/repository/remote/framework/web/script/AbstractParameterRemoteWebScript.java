package fr.openwide.alfresco.repository.remote.framework.web.script;

import java.io.IOException;
import java.net.URLDecoder;

import org.springframework.extensions.webscripts.WebScriptRequest;

import fr.openwide.alfresco.repository.api.remote.model.endpoint.RestEndpoint;

/**
 * Base class for web services that provide automatic one parameter handling. 
 * Message must be a unique object in a request header, serialized with Jackson and of type « P »
 */
public abstract class AbstractParameterRemoteWebScript<R, P> extends AbstractMessageRemoteWebScript<R, P> {

	@Override
	protected String getRawPayload(WebScriptRequest req) throws IOException {
		return URLDecoder.decode(req.getHeader(RestEndpoint.HEADER_MESSAGE_CONTENT), "UTF-8");
	}

}
