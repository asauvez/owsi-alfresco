package fr.openwide.alfresco.repository.remote.framework.web.script;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;

import javax.transaction.UserTransaction;

import net.sf.acegisecurity.AccessDeniedException;

import org.alfresco.repo.node.integrity.IntegrityException;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.transaction.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Container;
import org.springframework.extensions.webscripts.Description;
import org.springframework.extensions.webscripts.Description.RequiredTransaction;
import org.springframework.extensions.webscripts.Description.RequiredTransactionParameters;
import org.springframework.extensions.webscripts.Description.TransactionCapability;
import org.springframework.extensions.webscripts.DescriptionImpl;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.TransactionParameters;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.openwide.alfresco.repository.api.remote.exception.AccessDeniedRemoteException;
import fr.openwide.alfresco.repository.api.remote.exception.IllegalStateRemoteException;
import fr.openwide.alfresco.repository.api.remote.exception.IntegrityRemoteException;
import fr.openwide.alfresco.repository.api.remote.exception.InvalidMessageRemoteException;
import fr.openwide.alfresco.repository.api.remote.exception.RepositoryRemoteException;
import fr.openwide.alfresco.repository.remote.framework.exception.InvalidPayloadException;
import fr.openwide.alfresco.repository.remote.framework.model.InnerTransactionParameters;

/**
 * Base class which handle web-service returning one object of type R.
 * 
 * Implementations must provide {@link AbstractRemoteWebScript#executeImpl(WebScriptRequest, WebScriptResponse, Status, Cache)}
 * 
 * This method may throw {@link RuntimeException} or {@link RepositoryRemoteException}
 * For any {@link RepositoryRemoteException}, the exception is logged then sent serialized as JSON to the remote client.
 * For {@link RuntimeException}, exception is logged and encapsulated inside {@link IllegalStateRepositoryRemoteException} 
 * to be sent to the remote client.
 * 
 * Exception's qualified class name is added in the header named {@link RepositoryRemoteException#HEADER_EXCEPTION_CLASS_NAME}
 * 
 * {@see org.springframework.extensions.webscripts.DeclarativeWebScript}
 * {@see org.alfresco.repo.web.scripts.content.StreamContent}
 * {@see org.springframework.extensions.webscripts.DeclarativeRegistry}
 * {@see org.alfresco.repo.web.scripts.RepositoryContainer}
 * {@see org.springframework.extensions.webscripts.DescriptionImpl#parse(org.dom4j.Element)}
 */
public abstract class AbstractRemoteWebScript<R> extends AbstractWebScript {

	private static final String KEY_INNER_TRANSACTION = "innerTransaction";
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	protected TransactionService transactionService;
	protected ObjectMapper objectMapper;

	@Override
	public void init(Container container, Description description) {
		super.init(container, description);
		// Retrieve transaction parameters
		if (description instanceof DescriptionImpl) {
			DescriptionImpl descriptionImpl = (DescriptionImpl) description;
			HashMap<String, Serializable> extensions = new HashMap<>();
			descriptionImpl.setExtensions(extensions);
			// Add transaction parameters on the webscript's description extension (inner)
			RequiredTransactionParameters transactionParameters = description.getRequiredTransactionParameters();
			InnerTransactionParameters inner = InnerTransactionParameters.build(transactionParameters);
			extensions.put(KEY_INNER_TRANSACTION, inner);
			// Remove transaction parameters on the container (outer)
			TransactionParameters outer = new TransactionParameters();
			outer.setRequired(RequiredTransaction.none);
			descriptionImpl.setRequiredTransactionParameters(outer);
		} else {
			throw new IllegalStateRemoteException("Could not alter webscript description: " + description.getClass());
		}
	}

	@Override
	public final void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
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
		R resValue = null;
		Exception resException = null;
		int statusCode;
		try {
			resValue = transactionedExecute(req, res, status, cache);
			statusCode = status.getCode();
		} catch (AccessDeniedRemoteException | AccessDeniedException e) {
			logger.warn("Could not get access", e);
			resException = e;
			statusCode = Status.STATUS_FORBIDDEN;
		} catch (InvalidPayloadException e) {
			String message = buildExceptionMessage("Could not use payload", e);
			logger.warn(message, e);
			// any invalid payload exception is encapsulated inside InvalidMessageRemoteException which can be serialized
			resException = new InvalidMessageRemoteException(message, e);
			statusCode = Status.STATUS_BAD_REQUEST;
		} catch (InvalidMessageRemoteException e) {
			logger.warn("Could not parse message", e);
			resException = e;
			statusCode = Status.STATUS_BAD_REQUEST;
		} catch (RepositoryRemoteException e) {
			logger.warn("Could not execute request", e);
			resException = e;
			statusCode = Status.STATUS_INTERNAL_SERVER_ERROR;
		} catch (IntegrityException e) {
			logger.warn("IntegrityException", e);
			resException = new IntegrityRemoteException(e);
			statusCode = Status.STATUS_INTERNAL_SERVER_ERROR;
		} catch (Throwable e) {
			// any unexpected exception is encapsulated inside IllegalStateRemoteException which can be serialized
			String message = buildExceptionMessage("Unexpected error occured", e);
			logger.error(message, e);
			resException = new IllegalStateRemoteException(message, e);
			statusCode = Status.STATUS_INTERNAL_SERVER_ERROR;
		}
		if (resValue == null && statusCode == Status.STATUS_OK) {
			status.setCode(Status.STATUS_NO_CONTENT);
		}
		// is a redirect to a status specific template required?
		if (status.getRedirect()) {
			throw new WebScriptException("Web Script redirection is not supported");
		} else {
			// force status
			if (statusCode != Status.STATUS_OK && ! req.forceSuccessStatus()) {
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
			if (resValue != null) {
				handleResult(res, resValue);
			} else if (resException != null) {
				setExceptionHeader(res, resException);
				objectMapper.writeValue(res.getOutputStream(), resException);
			}
		}
	}

	protected R transactionedExecute(final WebScriptRequest req, final WebScriptResponse res, 
			final Status status, final Cache cache) throws Exception {
		RequiredTransactionParameters transaction = 
				(RequiredTransactionParameters) getDescription().getExtensions().get(KEY_INNER_TRANSACTION);
		if (RequiredTransaction.none.equals(transaction.getRequired())) {
			// no transaction
			return executeImpl(req, res, status, cache);
		} else {
			// do in transaction
			RetryingTransactionCallback<R> work = new RetryingTransactionCallback<R>() {
				@Override
				public R execute() throws Exception {
					try {
						return executeImpl(req, res, status, cache);
					} catch (Exception e) {
						UserTransaction txn = RetryingTransactionHelper.getActiveUserTransaction();
						if (txn != null && txn.getStatus() != javax.transaction.Status.STATUS_MARKED_ROLLBACK) {
							try {
								txn.setRollbackOnly();
							} catch (Throwable se) {
								e.addSuppressed(se);
							}
						}
						// re-throw original exception
						throw e;
					}
				}
			};
			boolean readonly = TransactionCapability.readonly.equals(transaction.getCapability());
			boolean requiresNew = RequiredTransaction.requiresnew.equals(transaction.getRequired());
			return transactionService.getRetryingTransactionHelper().doInTransaction(work, readonly, requiresNew);
		}
	}

	protected abstract R executeImpl(WebScriptRequest req, WebScriptResponse res, Status status, Cache cache);

	protected void handleResult(WebScriptResponse res, R resValue) throws IOException {
		objectMapper.writeValue(res.getOutputStream(), resValue);
	}

	protected static void setExceptionHeader(WebScriptResponse res, Exception e) {
		res.setHeader(RepositoryRemoteException.HEADER_EXCEPTION_CLASS_NAME, e.getClass().getName());
	}

	protected static String buildExceptionMessage(String prefix, Throwable e) {
		StringBuilder message = new StringBuilder(prefix).append(": ").append(e.getMessage());
		if (e.getSuppressed() != null) {
			message.append("; Suppressed: ").append(Arrays.toString(e.getSuppressed()));
		}
		if (e.getCause() != null) {
			message.append("; Caused by: ").append(e.getCause());
		}
		return message.toString();
	}

	protected String getParameter(WebScriptRequest req, String name, String defaultValue) {
		String value = req.getParameter(name);
		if (! StringUtils.hasText(value)) {
			value = defaultValue;
		}
		return value;
	}

	protected String getRequiredParameter(WebScriptRequest req, String name) {
		String value = req.getParameter(name);
		if (! StringUtils.hasText(value)) {
			throw new InvalidMessageRemoteException("Could not get required parameter: " + name);
		}
		return value;
	}

	public void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}
	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

}
