package fr.openwide.alfresco.app.web.download.binding;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import fr.openwide.alfresco.api.core.node.binding.content.NodeContentDeserializer;
import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.node.model.RemoteCallParameters;
import fr.openwide.alfresco.api.core.node.model.RepositoryContentData;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.app.core.node.service.NodeService;
import fr.openwide.alfresco.app.core.search.service.impl.NodeSearchServiceImpl;
import fr.openwide.alfresco.app.web.download.model.ByteArrayDownloadResponse;
import fr.openwide.alfresco.app.web.download.model.ContentDownloadResponse;
import fr.openwide.alfresco.app.web.download.model.DownloadResponse;
import fr.openwide.alfresco.app.web.download.model.FileDownloadResponse;
import fr.openwide.alfresco.app.web.download.model.NodeReferenceDownloadResponse;
import fr.openwide.alfresco.app.web.validation.binding.ValidationResponseMethodProcessor;
import fr.openwide.alfresco.app.web.validation.model.AlertContainer;
import fr.openwide.core.spring.util.StringUtils;

public class DownloadResponseMethodProcessor implements HandlerMethodReturnValueHandler, HandlerMethodArgumentResolver {

	private static final Logger LOGGER = LoggerFactory.getLogger(NodeSearchServiceImpl.class);
	
	public static final String FILE_DOWNLOAD_TOKEN_NAME = "fileDownloadToken";
	public static final String FILE_DOWNLOAD_PATH_NAME = "fileDownloadPath";

	protected static final String HEADER_CONTENT_DISPOSITION_NAME = "Content-Disposition";
	protected static final String HEADER_CONTENT_DISPOSITION_VALUE_PATTERN = "{0};filename=\"{1}\"";

	protected NodeService nodeService;

	public DownloadResponseMethodProcessor(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		Class<?> parameterType = parameter.getParameterType();
		return DownloadResponse.class.isAssignableFrom(parameterType);
	}

	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		Class<?> parameterType = returnType.getParameterType();
		return DownloadResponse.class.isAssignableFrom(parameterType);
	}

	@Override
	public DownloadResponse resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
		HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
		DownloadResponse downloadResponse;
		
		Class<?> parameterType = parameter.getParameterType();
		if (ByteArrayDownloadResponse.class.equals(parameterType)) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			downloadResponse = new ByteArrayDownloadResponse(response, baos);
		} else if (FileDownloadResponse.class.equals(parameterType)) {
			File tempFile = File.createTempFile("download", Long.toString(System.nanoTime()));
			OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(tempFile));
			// store outputStream if needed during the request lifecycle
			request.setAttribute(getClass().getName(), outputStream);
			downloadResponse = new FileDownloadResponse(response, tempFile, outputStream);
		} else if (NodeReferenceDownloadResponse.class.equals(parameterType)) {
			downloadResponse = new NodeReferenceDownloadResponse(response);
		} else {
			throw new IllegalArgumentException("Invalid type: " + parameterType);
		}

		downloadResponse.contentRange(request.getHeader("Range"));
		
		return downloadResponse;
	}

	@Override
	public void handleReturnValue(Object returnValue, MethodParameter returnType,
			ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
		if (returnValue == null) {
			return;
		} else if (returnValue instanceof DownloadResponse) {
			mavContainer.setRequestHandled(true);
			DownloadResponse download = (DownloadResponse) returnValue;
			HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
			HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);

			// ensure attachment name
			ensureAttachmentName(download, request);
			// deal with AlertContainer for the subsequent request
			AlertContainer alertContainer = download.getAlertContainer();
			if (! alertContainer.isEmpty()) {
				// build flashmap for the specific targetRequestPath or for the next request if no information
				FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
				flashMap.put(AlertContainer.ALERTS_FIELD_NAME, alertContainer);
				// get targetRequestPath: may be null if request is not standard ajax
				String targetRequestPath = getFromRequestOrInputFlashMap(request, ValidationResponseMethodProcessor.TARGET_REQUEST_PATH_HEADER_NAME);
				if (targetRequestPath != null) {
					UriComponents uriComponents = UriComponentsBuilder.fromUriString(targetRequestPath).build();
					flashMap.setTargetRequestPath(uriComponents.getPath());
					flashMap.addTargetRequestParams(uriComponents.getQueryParams());
				}
				// save flashmap
				FlashMapManager flashMapManager = RequestContextUtils.getFlashMapManager(request);
				flashMapManager.saveOutputFlashMap(flashMap, request, response);
			}
			// stream input
			if (download instanceof ContentDownloadResponse) {
				try (ContentDownloadResponse content = (ContentDownloadResponse) download) {
					streamContent(content, webRequest);
				}
			} else if (download instanceof NodeReferenceDownloadResponse) {
				streamNodeReference((NodeReferenceDownloadResponse) download, webRequest);
			} else {
				// should not happen
				throw new UnsupportedOperationException("Unexpected return type: " +
						returnType.getParameterType().getName() + " in method: " + returnType.getMethod());
			}
		} else {
			// should not happen
			throw new UnsupportedOperationException("Unexpected return type: " +
					returnType.getParameterType().getName() + " in method: " + returnType.getMethod());
		}
	}

	protected void streamContent(ContentDownloadResponse download, NativeWebRequest webRequest) throws IOException {
		HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
		// flush download output
		download.flush();
		// set cookie (do this before header content-length so that client can deal with it early !)
		setCookie(webRequest);
		// set mimetype and length for the content
		response.setContentType(download.getContentType());
		// output downloadable content
		streamInput(download, download.getContentLength(), true, download.getWrittableStream(), response);
	}

	protected void streamNodeReference(final NodeReferenceDownloadResponse download, final NativeWebRequest webRequest) {
		if (download.getNodeReference() == null) {
			throw new IllegalArgumentException("nodeReference is mandatory");
		}

		NodeScope nodeScope = new NodeScope();
		NodeScope downloadNodeScope = nodeScope;
		NameReference renditionName = download.rendition().getName();
		if (renditionName != null) {
			downloadNodeScope = new NodeScope();
			nodeScope.getRenditions().put(renditionName, downloadNodeScope);
		}
		
		downloadNodeScope.getProperties().add(download.getProperty());
		downloadNodeScope.getContentDeserializers().put(download.getProperty(), new NodeContentDeserializer<Void>() {
			@Override
			public Void deserialize(RepositoryNode node, NameReference contentProperty, InputStream inputStream) throws IOException {
				// set cookie (do this before header content-length so that client can deal with it early !)
				setCookie(webRequest);

				HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
				RepositoryContentData data = node.getProperty(contentProperty, RepositoryContentData.class);
				streamNodeContentData(data, response, download, inputStream);
				
				return null;
			}
		});
		
		// Range géré coté Alfresco
		download.getRemoteCallParameters().contentRangeStart(download.getContentRangeStart());
		download.getRemoteCallParameters().contentRangeEnd(download.getContentRangeEnd());
		
		RemoteCallParameters.execute(download.getRemoteCallParameters(), new Callable<RepositoryNode>() {
			@Override
			public RepositoryNode call() throws Exception {
				return nodeService.get(download.getNodeReference(), nodeScope);
			}
		});
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

	protected void streamInput(DownloadResponse download, long contentLength, boolean manageRange, 
			InputStream input, HttpServletResponse response) throws IOException {
		setContentDispositionHeader(download, response);

		if (download.getContentWatermark() != null) {
			contentLength += download.getContentWatermark().length;
			input = new SequenceInputStream(input, new ByteArrayInputStream(download.getContentWatermark()));
		}
		
		// set content-length manually rather than using setContentLength to allow for size as long
		contentLength = download.getContentLength(contentLength);
		String range = download.getContentRange(contentLength); 

		response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
		response.setHeader(HttpHeaders.CONTENT_LENGTH, Long.toString(contentLength));
		if (range != null) {
			response.setHeader(HttpHeaders.CONTENT_RANGE, download.getContentRange(contentLength));
		}
		
		try {
			ServletOutputStream output = response.getOutputStream();
			if (! manageRange || (download.getContentRangeStart() == null && download.getContentRangeEnd() == null)) {
				IOUtils.copy(input, output);
			} else {
				long inputOffset = (download.getContentRangeStart() != null) ? download.getContentRangeStart() : 0L;
				long length = ((download.getContentRangeEnd() != null) ? download.getContentRangeEnd() : contentLength)
						 - inputOffset;
				IOUtils.copyLarge(input, output, inputOffset, length);
			}
		} catch (Exception ex) {
			if ("org.apache.catalina.connector.ClientAbortException: java.net.SocketException: Connection reset".equals(ex.toString())) {
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

	protected static String ensureAttachmentName(DownloadResponse download, HttpServletRequest request) {
		// ensure attachment name
		String attachmentName;
		if (download.getAttachmentName() == null) {
			attachmentName = FilenameUtils.getName(request.getRequestURI());
		} else {
			String extension = FilenameUtils.getExtension(download.getAttachmentName());
			if (StringUtils.hasText(extension)) {
				attachmentName = StringUtils.urlize(FilenameUtils.getBaseName(download.getAttachmentName())) + "." + StringUtils.urlize(extension);
			} else {
				attachmentName = StringUtils.urlize(FilenameUtils.getName(download.getAttachmentName()));
			}
		}
		download.attachmentName(attachmentName);
		return attachmentName;
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
