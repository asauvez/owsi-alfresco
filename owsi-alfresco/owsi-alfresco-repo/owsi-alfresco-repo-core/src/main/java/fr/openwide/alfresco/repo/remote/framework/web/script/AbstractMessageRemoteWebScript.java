package fr.openwide.alfresco.repo.remote.framework.web.script;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import com.fasterxml.jackson.databind.JavaType;

/**
 * Base class for web services that provide automatic one argument handling. Payload must be a unique object in message body,
 * serialized with Jackson and of type « P »
 */
public abstract class AbstractMessageRemoteWebScript<R, P> extends AbstractRemoteWebScript<R, P> {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Override
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
			throw new IllegalStateException("Failed to parse argument of type " + getParameterType(), e);
		}
	}

	@Override
	protected R executeImpl(P payload) {
		return execute(payload);
	}

	protected abstract R execute(P payload);

	@Override
	protected void handleResult(WebScriptResponse res, R resValue) throws IOException {
		res.setContentType("application/json;charset=UTF-8");
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Serializing result: {}", objectMapper.writeValueAsString(resValue));
		}
		objectMapper.writeValue(res.getOutputStream(), resValue);
	}

	protected String getRawPayload(WebScriptRequest req) throws IOException {
		return req.getContent().getContent();
	}

	/**
	 * Provide {@link JavaType} used to unserialize the only argument. If null, body is not parsed and null is passed
	 * as the payload to {@link AbstractRemoteWebScript#executeImpl(WebScriptRequest, WebScriptResponse, Status, Cache)}
	 */
	protected Class<P> getParameterType() {
		return null;
	}

}
