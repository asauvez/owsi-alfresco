package fr.openwide.alfresco.query.repo.web.scripts;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.util.StringUtils;

import fr.openwide.alfresco.query.repo.exception.AbstractRemoteException;
import fr.openwide.alfresco.query.repo.exception.RemoteArgumentException;
import fr.openwide.alfresco.query.repo.exception.RemoteUnknownException;
import fr.openwide.alfresco.query.repo.mapper.ObjectMapperProvider;


/**
 * Base class which handle web-service returning one object of type R and Serializable.
 * 
 * Implementations must provides {@link AbstractJacksonJavaBackedWebScript#executeImpl(WebScriptRequest, WebScriptResponse, Status, Cache)}
 * 
 * This method may throw {@link RuntimeException}, {@link AbstractRemoteException} or {@link RemoteArgumentException}.
 * For any {@link AbstractRemoteException}, the exception is logged then sent serialized as JSON to the remote client.
 * For {@link RuntimeException}, exception is logged and encapsulated inside {@link RemoteUnknownException} to be sent
 * to remote client.
 * 
 * Exception qualified class name is added in header with name {@link AbstractRemoteException#HEADER_EXCEPTION_CLASS_NAME}
 * 
 * {@see DeclarativeWebScript}
 * {@see StreamContent}
 */
public abstract class AbstractJacksonJavaBackedWebScript<R> extends AbstractWebScript {

	private final Log logger = LogFactory.getLog(this.getClass());

	protected ObjectMapperProvider objectMapperProvider;

	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		// retrieve requested format
		String format = req.getFormat();
		if (!WebScriptResponse.JSON_FORMAT.equals(format)) {
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
			statusCode = status.getCode();
		} catch (RemoteArgumentException e) {
			// extractParameter fails
			model = e;
			String message = "Error processing arguments (sent to client) : " + e.getMessage();
			logger.error(message, e);
			statusCode = Status.STATUS_BAD_REQUEST;
			res.setHeader(AbstractRemoteException.HEADER_EXCEPTION_CLASS_NAME, e.getClass().getName());
			if (txn != null) {
				try {
					txn.setRollbackOnly();
				} catch (SystemException se) {
					throw new IllegalStateException("Error rollbacking transaction : " + se.getMessage(), e);
				}
			}
		} catch (AbstractRemoteException e) {
			// any known exception which can be serialized to be sent over the network
			model = e;
			String message = "Remote exception (sent to client) : " + e.getMessage();
			logger.error(message, e);
			statusCode = Status.STATUS_INTERNAL_SERVER_ERROR;
			res.setHeader(AbstractRemoteException.HEADER_EXCEPTION_CLASS_NAME, e.getClass().getName());
			if (txn != null) {
				try {
					txn.setRollbackOnly();
				} catch (SystemException se) {
					throw new IllegalStateException("Error rollbacking transaction : " + se.getMessage(), e);
				}
			}
		} catch (Throwable e) {
			// any unknown exception is encapsulated inside RemoteUnknownException which can be serialized
			// concatenate cause message because cause is not serialized
			String message = "Unknown remote error (sent to client) : " + e.getMessage();
			logger.error(message, e);
			model = new RemoteUnknownException(message, e);
			res.setHeader(AbstractRemoteException.HEADER_EXCEPTION_CLASS_NAME, RemoteUnknownException.class.getName());
			statusCode = Status.STATUS_INTERNAL_SERVER_ERROR;
			if (txn != null) {
				try {
					txn.setRollbackOnly();
				} catch (SystemException se) {
					throw new IllegalStateException("Error rollbacking transaction : " + se.getMessage(), e);
				}
			}
		}
		
		// is a redirect to a status specific template required?
		if (status.getRedirect()) {
			sendStatus(req, res, status, cache, format, createTemplateParameters(req, res, new HashMap<String, Object>()));
		} else {
			// force status
			if (statusCode != HttpServletResponse.SC_OK && !req.forceSuccessStatus()) {
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
			objectMapperProvider.getMapper().writeValue(res.getOutputStream(), model);
		}
	}

	protected String getParameter(WebScriptRequest req, String name, String defaultValue) {
		String value = req.getParameter(name);
		if (!StringUtils.hasText(value)) {
			value = defaultValue;
		}
		return value;
	}

	protected String getMandatoryParameter(WebScriptRequest req, String name) throws RemoteArgumentException {
		String value = req.getParameter(name);
		if (!StringUtils.hasText(value)) {
			throw new RemoteArgumentException("Argument " + name + " must not be empty");
		}
		return value;
	}

	protected abstract R executeImpl(WebScriptRequest req, WebScriptResponse res, Status status, Cache cache) throws AbstractRemoteException;

	public void setObjectMapperProvider(ObjectMapperProvider objectMapperProvider) {
		this.objectMapperProvider = objectMapperProvider;
	}

}
