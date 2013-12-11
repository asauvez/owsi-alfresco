package fr.openwide.alfresco.repository.remote.framework.web.script;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.UserTransaction;

import net.sf.acegisecurity.AccessDeniedException;

import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.openwide.alfresco.repository.api.remote.model.AccessDeniedRemoteException;
import fr.openwide.alfresco.repository.api.remote.model.IllegalStateRemoteException;
import fr.openwide.alfresco.repository.api.remote.model.InvalidPayloadRemoteException;
import fr.openwide.alfresco.repository.api.remote.model.RepositoryRemoteException;

/**
 * Base class which handle web-service returning one object of type R and Serializable.
 * 
 * Implementations must provides {@link AbstractRemoteWebScript#executeImpl(WebScriptRequest, WebScriptResponse, Status, Cache)}
 * 
 * This method may throw {@link RuntimeException} or {@link RepositoryRemoteException}
 * For any {@link RepositoryRemoteException}, the exception is logged then sent serialized as JSON to the remote client.
 * For {@link RuntimeException}, exception is logged and encapsulated inside {@link IllegalStateAlfrescoRemoteException} to be sent
 * to remote client.
 * 
 * Exception qualified class name is added in header with name {@link RepositoryRemoteException#HEADER_EXCEPTION_CLASS_NAME}
 * 
 * {@see DeclarativeWebScript}
 * {@see StreamContent}
 */
public abstract class AbstractRemoteWebScript<R> extends AbstractWebScript {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	protected ObjectMapper objectMapper;

	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		// retrieve requested format
		String format = req.getFormat();
		if (! WebScriptResponse.JSON_FORMAT.equals(format)) {
			throw new WebScriptException("Web Script format '" + format + "' is not JSON");
		}
		// establish mimetype from format
		String mimetype = getContainer().getFormatRegistry().getMimeType(req.getAgent(), format);
		if (mimetype == null) {
			throw new WebScriptException("Web Script format '" + format + "' is not registered");
		}
		// construct model for script / template
		Status status = new Status();
		Cache cache = new Cache(getDescription().getRequiredCache());
		Object model;
		int statusCode;
		// may be null
		UserTransaction txn = RetryingTransactionHelper.getActiveUserTransaction();
		try {
			model = executeImpl(req, res, status, cache);
			if (model == null && status.getCode() == HttpServletResponse.SC_OK) {
				status.setCode(Status.STATUS_NO_CONTENT);
			}
			statusCode = status.getCode();
		} catch (AccessDeniedRemoteException | AccessDeniedException  e) {
			model = e;
			logger.warn("Could not get access", e);
			statusCode = Status.STATUS_FORBIDDEN;
			res.setHeader(RepositoryRemoteException.HEADER_EXCEPTION_CLASS_NAME, e.getClass().getName());
			setRollbackOnly(txn, e);
		} catch (InvalidPayloadRemoteException e) {
			model = e;
			logger.warn("Could not extract payload: " + e.getMessage(), e);
			statusCode = Status.STATUS_BAD_REQUEST;
			res.setHeader(RepositoryRemoteException.HEADER_EXCEPTION_CLASS_NAME, e.getClass().getName());
			setRollbackOnly(txn, e);
		} catch (RepositoryRemoteException e) {
			model = e;
			logger.warn("Could not execute request: " + e.getMessage(), e);
			statusCode = Status.STATUS_INTERNAL_SERVER_ERROR;
			res.setHeader(RepositoryRemoteException.HEADER_EXCEPTION_CLASS_NAME, e.getClass().getName());
			setRollbackOnly(txn, e);
		} catch (Throwable e) {
			// any unexpected exception is encapsulated inside BartocRemoteUnknownException which can be serialized
			// concatenate cause message because cause is not serialized
			String message = "Unexpected error occured: " + e.getMessage();
			logger.error(message, e);
			model = new IllegalStateRemoteException(message, e);
			res.setHeader(RepositoryRemoteException.HEADER_EXCEPTION_CLASS_NAME, model.getClass().getName());
			statusCode = Status.STATUS_INTERNAL_SERVER_ERROR;
			setRollbackOnly(txn, (Throwable) model);
		}
		
		// is a redirect to a status specific template required?
		if (status.getRedirect()) {
			throw new WebScriptException("Web Script redirection is not supported");
		} else {
			// force status
			if (statusCode != HttpServletResponse.SC_OK && ! req.forceSuccessStatus()) {
				if (logger.isDebugEnabled()) {
					logger.debug("Force success status header in response: " + req.forceSuccessStatus());
					logger.debug("Setting status " + statusCode);
				}
				res.setStatus(statusCode);
			}
			// apply location
			String location = status.getLocation();
			if (location != null && location.length() > 0) {
				if (logger.isDebugEnabled()) {
					logger.debug("Setting location to " + location);
				}
				res.setHeader(WebScriptResponse.HEADER_LOCATION, location);
			}
			// apply cache
			res.setCache(cache);
			// apply content type and charset
			res.setContentType(mimetype + ";charset=UTF-8");
			if (logger.isDebugEnabled()) {
				logger.debug("Rendering response: content type=" + mimetype + ", status=" + statusCode);
			}
			// render response according to model
			if (model != null) {
				objectMapper.writeValue(res.getOutputStream(), model);
			}
		}
	}

	protected void setRollbackOnly(UserTransaction txn, Throwable e) {
		if (txn != null) {
			try {
				txn.setRollbackOnly();
			} catch (Throwable se) {
				logger.error("Could not rollback transaction", se);
				e.addSuppressed(se);
			}
		}
	}

	protected String getParameter(WebScriptRequest req, String name, String defaultValue) {
		String value = req.getParameter(name);
		if (!StringUtils.hasText(value)) {
			value = defaultValue;
		}
		return value;
	}

	protected String getRequiredParameter(WebScriptRequest req, String name) throws InvalidPayloadRemoteException {
		String value = req.getParameter(name);
		if (!StringUtils.hasText(value)) {
			throw new InvalidPayloadRemoteException("Could not :" + name);
		}
		return value;
	}

	protected abstract R executeImpl(WebScriptRequest req, WebScriptResponse res, Status status, Cache cache) throws RepositoryRemoteException;

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

}
