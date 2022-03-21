package fr.openwide.alfresco.app.web.download.binding;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.text.MessageFormat;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.support.RequestContextUtils;

import fr.openwide.alfresco.api.core.node.model.RepositoryContentData;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.app.web.download.model.DownloadResponse;
import fr.openwide.alfresco.app.web.download.model.NodeReferenceDownloadResponse;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.core.spring.util.StringUtils;

public abstract class DownloadResponseHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DownloadResponseHandler.class);
	
	public static final String FILE_DOWNLOAD_TOKEN_NAME = "fileDownloadToken";
	public static final String FILE_DOWNLOAD_PATH_NAME = "fileDownloadPath";

	protected static final String HEADER_CONTENT_DISPOSITION_NAME = "Content-Disposition";
	protected static final String HEADER_CONTENT_DISPOSITION_VALUE_PATTERN = "{0};filename=\"{1}\"";
	
	
	public void initNodeScope(
			@SuppressWarnings("unused") NodeReferenceDownloadResponse download,
			@SuppressWarnings("unused") NodeScopeBuilder nodeScopeBuilder) {
		// A surcharger si besoin de valeurs supplémentaires
	}

	public void handleContentNode(
			@SuppressWarnings("unused") NodeReferenceDownloadResponse download,
			@SuppressWarnings("unused") BusinessNode node) {
		// A surcharger si besoin de valeurs supplémentaires
	}

	public void deserialize(
			NodeReferenceDownloadResponse download, RepositoryNode node, 
			NameReference contentProperty, 
			NativeWebRequest webRequest, InputStream inputStream) throws IOException {
		BusinessNode businessNode = new BusinessNode(node);
		BusinessNode businessNodePrincipal = (download.getNodeReference().equals(node.getNodeReference())) 
			? businessNode : businessNode.assocs().primaryParent();
		
		handleContentNode(download, businessNodePrincipal);
		
		// set cookie (do this before header content-length so that client can deal with it early !)
		setCookie(webRequest);

		HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
		RepositoryContentData data = node.getProperty(contentProperty, RepositoryContentData.class);
		streamNodeContentData(data, response, download, inputStream);
	}
	
	/**
	 * Method can be overriden (not static) to deal with specific input
	 */
	protected void streamNodeContentData(RepositoryContentData data, HttpServletResponse response,
			NodeReferenceDownloadResponse download, InputStream inputStream) throws IOException {
		response.setContentType(data.getMimetype());
		response.setCharacterEncoding(data.getEncoding());
		
		streamInput(download, data.getSize(), false, inputStream, response);
	}

	protected void streamInput(DownloadResponse download, long fullContentLength, boolean manageRange, 
			InputStream input, HttpServletResponse response) throws IOException {
		setContentDispositionHeader(download, response);

		if (download.getContentWatermark() != null) {
			fullContentLength += download.getContentWatermark().length;
			input = new SequenceInputStream(input, new ByteArrayInputStream(download.getContentWatermark()));
		}
		
		// set content-length manually rather than using setContentLength to allow for size as long
		int status = download.getHttpStatus();
		String range = download.getContentRange(fullContentLength); 
		long packetContentLength = download.getContentLength(fullContentLength);

		response.setStatus(status);
		response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
		response.setHeader(HttpHeaders.CONTENT_LENGTH, Long.toString(packetContentLength));
		if (range != null) {
			response.setHeader(HttpHeaders.CONTENT_RANGE, range);
		}
		
		try {
			ServletOutputStream output = response.getOutputStream();
			if (! manageRange || ! download.hasContentRange()) {
				IOUtils.copy(input, output);
			} else {
				long inputOffset = (download.getContentRangeStart() != null) ? download.getContentRangeStart() : 0L;
				long length = ((download.getContentRangeEnd() != null) ? download.getContentRangeEnd() : packetContentLength)
						 - inputOffset;
				IOUtils.copyLarge(input, output, inputOffset, length);
			}
		} catch (Exception ex) {
			if ("org.apache.catalina.connector.ClientAbortException".equals(ex.getClass().getName())) {
				LOGGER.warn("ClientAbortException : Normal when using pdf.js or when the user close the navigator before the end of the download");
			} else {
				throw ex;
			}
		}
	}
	
	protected static void setContentDispositionHeader(DownloadResponse download, HttpServletResponse response) {
		String headerValue = MessageFormat.format(HEADER_CONTENT_DISPOSITION_VALUE_PATTERN, 
				(download.isAttachment() ? "attachment" : "inline"), download.getAttachmentName());
		response.setHeader(HEADER_CONTENT_DISPOSITION_NAME, headerValue);
	}
	
	protected static void setCookie(NativeWebRequest webRequest) {
		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
		HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
		String salt = getFromRequestOrInputFlashMap(request, FILE_DOWNLOAD_TOKEN_NAME);
		if (salt != null) {
			Cookie cookie = new Cookie(FILE_DOWNLOAD_TOKEN_NAME, salt);
			String path = getFromRequestOrInputFlashMap(request, FILE_DOWNLOAD_PATH_NAME);
			if (path != null) {
				cookie.setPath(path);
			} else {
				cookie.setPath(StringUtils.trimTrailingCharacter(FilenameUtils.getFullPath(request.getRequestURI()), '/'));
			}
			response.addCookie(cookie);
		}
	}

	protected static String getFromRequestOrInputFlashMap(HttpServletRequest request, String name) {
		String value = request.getParameter(name);
		if (value != null) {
			return value;
		}
		Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
		if (inputFlashMap == null) {
			return null;
		}
		return (String) inputFlashMap.get(name);
	}

}
