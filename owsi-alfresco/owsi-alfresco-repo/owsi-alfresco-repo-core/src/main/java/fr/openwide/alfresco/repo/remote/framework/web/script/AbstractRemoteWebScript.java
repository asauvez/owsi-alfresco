package fr.openwide.alfresco.repo.remote.framework.web.script;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.transaction.UserTransaction;

import org.alfresco.repo.domain.node.NodeExistsException;
import org.alfresco.repo.node.integrity.IntegrityException;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.TempFileProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import fr.openwide.alfresco.api.core.node.exception.NoSuchNodeRemoteException;
import fr.openwide.alfresco.api.core.node.exception.NodeExistsRemoteException;
import fr.openwide.alfresco.api.core.remote.exception.AccessDeniedRemoteException;
import fr.openwide.alfresco.api.core.remote.exception.IllegalStateRemoteException;
import fr.openwide.alfresco.api.core.remote.exception.IntegrityRemoteException;
import fr.openwide.alfresco.api.core.remote.exception.InvalidMessageRemoteException;
import fr.openwide.alfresco.api.core.remote.exception.RepositoryRemoteException;
import fr.openwide.alfresco.api.core.util.ThresholdBufferFactory;
import fr.openwide.alfresco.repo.core.configurationlogger.AlfrescoGlobalProperties;
import fr.openwide.alfresco.repo.remote.framework.exception.InvalidPayloadException;
import fr.openwide.alfresco.repo.remote.framework.model.InnerTransactionParameters;

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
public abstract class AbstractRemoteWebScript<R, P> extends AbstractWebScript {

	private static final String KEY_INNER_TRANSACTION = "innerTransaction";
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired protected TransactionService transactionService;
	@Autowired private AlfrescoGlobalProperties alfrescoGlobalProperties;
	
	protected ObjectMapper objectMapper = new ObjectMapper();
	private int maxRetries = 0;

	// @see RepositoryContainer
	private boolean encryptTempFiles = false;
	private String tempDirectoryName = null;
	private int memoryThreshold = 4 * 1024 * 1024; // 4mb
	private long maxContentSize = (long) 4 * 1024 * 1024 * 1024; // 4gb
	private ThresholdBufferFactory streamFactory = null;

	@Override
	public void init(Container container, Description description) {
		super.init(container, description);

		maxRetries = alfrescoGlobalProperties.getPropertyInt("server.transaction.max-retries");
		encryptTempFiles = alfrescoGlobalProperties.getPropertyBoolean("webscripts.encryptTempFiles");
		tempDirectoryName = alfrescoGlobalProperties.getPropertyMandatory("webscripts.tempDirectoryName");
		memoryThreshold = alfrescoGlobalProperties.getPropertyInt("webscripts.memoryThreshold");
		maxContentSize = alfrescoGlobalProperties.getPropertyLong("webscripts.setMaxContentSize");
		
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
		
		File tempDirectory = TempFileProvider.getTempDir(tempDirectoryName);
		this.streamFactory = ThresholdBufferFactory.newInstance(tempDirectory, memoryThreshold, maxContentSize, encryptTempFiles);
	}

	@Override
	public final void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing {} method with uri: {}", getDescription().getMethod(), req.getPathInfo());
		}
		
		BufferedRequest bufferedRequest = null;
		if (maxRetries > 0) {
			// En cas de problème de concurrence, on veut rejouer la requête. On ne peut rejouer la requête que si
			// on a bufferiser son contenu. C'est un mécanisme équivalent à ce que l'on trouve dans @see RepositoryContainer.
			bufferedRequest = new BufferedRequest(req, streamFactory);
			req = bufferedRequest;
		}
		
		P payload = extractPayload(req);
		
		// construct model for script / template
		R resValue = null;
		Exception resException = null;
		int statusCode;
		try {
			resValue = transactionedExecute(payload, bufferedRequest);
			statusCode = (resValue != null) ? Status.STATUS_OK : Status.STATUS_NO_CONTENT;
		} catch (InvalidNodeRefException e) {
			LOGGER.warn("Node does not exist", e);
			resException = new NoSuchNodeRemoteException(e);
			statusCode = Status.STATUS_NOT_FOUND;
		} catch (AccessDeniedRemoteException | AccessDeniedException e) {
			LOGGER.warn("Could not get access", e);
			resException = e;
			statusCode = Status.STATUS_FORBIDDEN;
		} catch (InvalidPayloadException e) {
			String message = buildExceptionMessage("Could not use payload", e);
			LOGGER.warn(message, e);
			// any invalid payload exception is encapsulated inside InvalidMessageRemoteException which can be serialized
			resException = new InvalidMessageRemoteException(message, e);
			statusCode = Status.STATUS_BAD_REQUEST;
		} catch (InvalidMessageRemoteException e) {
			LOGGER.warn("Could not parse message", e);
			resException = e;
			statusCode = Status.STATUS_BAD_REQUEST;
		} catch (RepositoryRemoteException e) {
			LOGGER.warn("Could not execute request", e);
			resException = e;
			statusCode = Status.STATUS_INTERNAL_SERVER_ERROR;
		} catch (IntegrityException e) {
			LOGGER.warn("Integrity error", e);
			resException = new IntegrityRemoteException(e);
			statusCode = Status.STATUS_INTERNAL_SERVER_ERROR;
		} catch (NodeExistsException e) {
			LOGGER.warn("Node exists", e);
			resException = new NodeExistsRemoteException(e);
			statusCode = Status.STATUS_INTERNAL_SERVER_ERROR;
		} catch (Throwable e) {
			// any unexpected exception is encapsulated inside IllegalStateRemoteException which can be serialized
			String message = buildExceptionMessage("Unexpected error occured", e);
			LOGGER.error(message, e);
			resException = new IllegalStateRemoteException(message, e);
			statusCode = Status.STATUS_INTERNAL_SERVER_ERROR;
		} finally {
			if (bufferedRequest != null) {
				bufferedRequest.close();
			}
		}
		// apply status code
		res.setStatus(statusCode);
		// apply cache
		res.setCache(new Cache(getDescription().getRequiredCache()));
		
		String agent = req.getHeader("User-Agent");
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, agent != null && agent.contains("Mozilla"));
		
		// render response according to model
		if (resException == null) {
			handleResult(res, resValue);
		} else {
			setExceptionHeader(res, resException);
			objectMapper.writeValue(res.getOutputStream(), resException);
		}
	}

	protected R transactionedExecute(final P parameter, final BufferedRequest bufferedRequest) throws Exception {
		RequiredTransactionParameters transaction = 
				(RequiredTransactionParameters) getDescription().getExtensions().get(KEY_INNER_TRANSACTION);
		if (RequiredTransaction.none.equals(transaction.getRequired())) {
			// no transaction
			return executeImpl(parameter);
		} else {
			// do in transaction
			RetryingTransactionCallback<R> work = new RetryingTransactionCallback<R>() {
				@Override
				public R execute() throws Exception {
					try {
						if (bufferedRequest != null) {
							bufferedRequest.rewind();
						}
						
						return executeImpl(parameter);
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
			
			RetryingTransactionHelper retryingTransactionHelper = transactionService.getRetryingTransactionHelper();
			retryingTransactionHelper.setMaxRetries(maxRetries);
			return retryingTransactionHelper.doInTransaction(work, readonly, requiresNew);
		}
	}

	protected abstract P extractPayload(WebScriptRequest req);
	protected abstract R executeImpl(P payload);
	protected abstract void handleResult(WebScriptResponse res, R resValue) throws IOException;

	protected static void setExceptionHeader(WebScriptResponse res, Exception e) {
		res.setContentType("application/json;charset=UTF-8");
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
	
	protected void setCache(WebScriptResponse res, int duration, TimeUnit unit, boolean isPublic) {
		Cache cache = new Cache();
		cache.setNeverCache(false);
		cache.setMustRevalidate(false);
		cache.setIsPublic(isPublic);
		cache.setMaxAge(unit.toSeconds(duration));
		res.setCache(cache);
	}
}
