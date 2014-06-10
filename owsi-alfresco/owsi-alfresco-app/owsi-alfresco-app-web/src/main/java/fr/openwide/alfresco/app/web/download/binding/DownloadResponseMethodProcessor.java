package fr.openwide.alfresco.app.web.download.binding;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import fr.openwide.alfresco.app.core.node.service.NodeService;
import fr.openwide.alfresco.app.web.download.model.ByteArrayDownloadResponse;
import fr.openwide.alfresco.app.web.download.model.ContentDownloadResponse;
import fr.openwide.alfresco.app.web.download.model.DownloadResponse;
import fr.openwide.alfresco.app.web.download.model.FileDownloadResponse;
import fr.openwide.alfresco.app.web.download.model.NodeReferenceDownloadResponse;
import fr.openwide.alfresco.app.web.validation.binding.ValidationResponseMethodProcessor;
import fr.openwide.alfresco.app.web.validation.model.AlertContainer;
import fr.openwide.core.spring.util.StringUtils;

public class DownloadResponseMethodProcessor implements HandlerMethodReturnValueHandler, HandlerMethodArgumentResolver {

	public static final String FILE_DOWNLOAD_TOKEN_NAME = "fileDownloadToken";
	public static final String FILE_DOWNLOAD_PATH_NAME = "fileDownloadPath";

	@Autowired
	private NodeService nodeService;
	
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		Class<?> paramType = parameter.getParameterType();
		return DownloadResponse.class.isAssignableFrom(paramType);
	}

	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		Class<?> paramType = returnType.getParameterType();
		return DownloadResponse.class.isAssignableFrom(paramType);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		Class<?> parameterType = parameter.getParameterType();
		if (ByteArrayDownloadResponse.class.equals(parameterType)) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			return new ByteArrayDownloadResponse(baos);
		} else if (FileDownloadResponse.class.equals(parameterType)) {
			File tempFile = File.createTempFile("download", Long.toString(System.nanoTime()));
			OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(tempFile));
			HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
			// store outputStream if needed during the request lifecycle
			request.setAttribute(getClass().getName(), outputStream);
			return new FileDownloadResponse(tempFile, outputStream);
		} else if (NodeReferenceDownloadResponse.class.equals(parameterType)) {
			return new NodeReferenceDownloadResponse(null);
		} else {
			throw new IllegalArgumentException("Invalid type: " + parameterType);
		}
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
			
			// get filename
			String fileName;
			if (download.getAttachementName() == null) {
				fileName = FilenameUtils.getName(request.getRequestURI());
			} else {
				String extension = FilenameUtils.getExtension(download.getAttachementName());
				if (StringUtils.hasText(extension)) {
					fileName = StringUtils.urlize(FilenameUtils.getBaseName(download.getAttachementName())) + "." + StringUtils.urlize(extension);
				} else {
					fileName = StringUtils.urlize(FilenameUtils.getName(download.getAttachementName()));
				}
			}
			
			// deal with AlertContainer for the subsequent request
			AlertContainer alertContainer = download.getAlertContainer();
			if (! alertContainer.isEmpty()) {
				HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
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
			
			// stream content
			if (download instanceof ContentDownloadResponse) {
				streamContent((ContentDownloadResponse) download, fileName, webRequest);
			} else if (download instanceof NodeReferenceDownloadResponse) {
				streamNodeRef((NodeReferenceDownloadResponse) download, fileName, webRequest);
			}
		} else {
			// should not happen
			throw new UnsupportedOperationException("Unexpected return type: " +
					returnType.getParameterType().getName() + " in method: " + returnType.getMethod());
		}
	}

	private void streamContent(ContentDownloadResponse download, String fileName, NativeWebRequest webRequest) throws IOException {
		HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
		try {
			// flush download output
			download.flush();
			// set cookie (do this before header content-length so that client can deal with it early !)
			setCookie(webRequest);

			// set mimetype and length for the content
			response.setContentType(download.getContentType());
			response.setHeader("Content-Length", Long.toString(download.getContentLength()));
			// output downloadable content
			streamCommon(download, fileName, response, download.getWrittableStream());
		} finally {
			download.close();
		}
	}

	private void streamNodeRef(final NodeReferenceDownloadResponse download, final String fileName, final NativeWebRequest webRequest) {
		// set cookie (do this before header content-length so that client can deal with it early !)
		
		final HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
		
		nodeService.getNodeContent(download.getNodeReference(), download.getProperty(), new ResponseExtractor<Void>() {
			@Override
			public Void extractData(ClientHttpResponse repositoryResponse) throws IOException {
				// set cookie (do this before header content-length so that client can deal with it early !)
				setCookie(webRequest);

				for (Entry<String, List<String>> entry : repositoryResponse.getHeaders().entrySet()) {
					for (String value : entry.getValue()) {
						response.addHeader(entry.getKey(), value);
					}
				}
				InputStream body = repositoryResponse.getBody();
				try {
					streamCommon(download, fileName, response, body);
				} finally {
					body.close();
				}
				return null;
			}
		});
	}
	
	private void streamCommon(DownloadResponse download, String fileName, HttpServletResponse response, InputStream in) throws IOException {
		response.setHeader("Content-Disposition", (download.isAttachement() ? "attachment" : "inline") + "; filename=\"" + fileName + "\"");
		IOUtils.copy(in, response.getOutputStream());
	}

	private void setCookie(NativeWebRequest webRequest) {
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

	private String getFromRequestOrInputFlashMap(HttpServletRequest request, String name) {
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
