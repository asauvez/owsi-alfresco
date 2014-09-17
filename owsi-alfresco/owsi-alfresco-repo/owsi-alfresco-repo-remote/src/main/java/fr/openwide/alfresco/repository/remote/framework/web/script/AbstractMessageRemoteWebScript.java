package fr.openwide.alfresco.repository.remote.framework.web.script;

import java.io.IOException;

import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import com.fasterxml.jackson.databind.JavaType;

import fr.openwide.alfresco.repository.api.remote.exception.InvalidMessageRemoteException;

/**
 * Base class for web services that provide automatic one argument handling. Payload must be a unique object in message body,
 * serialized with Jackson and of type « P »
 */
public abstract class AbstractMessageRemoteWebScript<R, P> extends AbstractRemoteWebScript<R> {

	@Override
	protected R executeImpl(WebScriptRequest req, WebScriptResponse res, Status status, Cache cache) {
		P payload = extractPayload(req);
		return executeImpl(payload);
	}

	protected P extractPayload(WebScriptRequest req) {
		P parameter;
		try {
			if (getParameterType() == null) {
				parameter = null;
			} else {
				parameter = objectMapper.readValue(getRawPayload(req), getParameterType());
			}
			
			return parameter;
		} catch (IOException e) {
			throw new InvalidMessageRemoteException("Failed to parse argument of type " + getParameterType(), e);
		}
	}

	protected String getRawPayload(WebScriptRequest req) throws IOException {
		return req.getContent().getContent();
	}

	protected abstract R executeImpl(P payload);

	/**
	 * Provide {@link JavaType} used to unserialize the only argument. If null, body is not parsed and null is passed
	 * as the payload to {@link AbstractRemoteWebScript#executeImpl(WebScriptRequest, WebScriptResponse, Status, Cache)}
	 */
	protected abstract JavaType getParameterType();

}
