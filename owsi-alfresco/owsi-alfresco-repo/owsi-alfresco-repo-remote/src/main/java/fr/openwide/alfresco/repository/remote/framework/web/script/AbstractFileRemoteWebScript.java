package fr.openwide.alfresco.repository.remote.framework.web.script;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.alfresco.util.TempFileProvider;
import org.apache.commons.io.IOUtils;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import fr.openwide.alfresco.repository.api.remote.model.RepositoryRemoteException;

/**
 * Base class for web-service which provides automatic two argument handling. First argument must be of type File 
 */
public abstract class AbstractFileRemoteWebScript<R, P> extends AbstractParameterRemoteWebScript<R, P> {

	@Override
	protected R executeImpl(WebScriptRequest req, WebScriptResponse res, Status status, Cache cache) throws RepositoryRemoteException {
		P payload = extractPayload(req, res);
		File tempFile = TempFileProvider.createTempFile("owsi-remote", ".file");
		OutputStream out = null;
		try {
			out = new FileOutputStream(tempFile);
			IOUtils.copy(req.getContent().getInputStream(), out);
			
			return executeImpl(tempFile, payload, status, cache);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		} finally {
			IOUtils.closeQuietly(out);
			tempFile.delete();
		}
	}

	protected abstract R executeImpl(File file, P payload, Status status, Cache cache) throws RepositoryRemoteException;

}
