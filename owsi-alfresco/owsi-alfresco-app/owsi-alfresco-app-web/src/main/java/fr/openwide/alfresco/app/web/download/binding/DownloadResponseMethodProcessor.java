package fr.openwide.alfresco.app.web.download.binding;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.MethodParameter;
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
import fr.openwide.alfresco.api.core.node.model.RemoteCallParameters;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.app.core.node.service.NodeService;
import fr.openwide.alfresco.app.web.download.model.ByteArrayDownloadResponse;
import fr.openwide.alfresco.app.web.download.model.ContentDownloadResponse;
import fr.openwide.alfresco.app.web.download.model.DownloadResponse;
import fr.openwide.alfresco.app.web.download.model.FileDownloadResponse;
import fr.openwide.alfresco.app.web.download.model.NodeReferenceDownloadResponse;
import fr.openwide.alfresco.app.web.validation.binding.ValidationResponseMethodProcessor;
import fr.openwide.alfresco.app.web.validation.model.AlertContainer;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.core.spring.util.StringUtils;

public class DownloadResponseMethodProcessor extends DownloadResponseHandler implements HandlerMethodReturnValueHandler, HandlerMethodArgumentResolver {

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

		DownloadResponseHandler alternateHandler = (download.getAlternateHandler() != null) ? download.getAlternateHandler() : DownloadResponseMethodProcessor.this;
		
		NodeScopeBuilder nodeScopeBuilder = new NodeScopeBuilder();
		
		NodeScopeBuilder downloadNodeScopeBuilder = nodeScopeBuilder;
		NameReference renditionName = download.rendition().getName();
		if (renditionName != null) {
			downloadNodeScopeBuilder = new NodeScopeBuilder();
			nodeScopeBuilder.getScope().getRenditions().put(renditionName, downloadNodeScopeBuilder.getScope());

			alternateHandler.initNodeScope(download, downloadNodeScopeBuilder);
			alternateHandler.initNodeScope(download, downloadNodeScopeBuilder.assocs().primaryParent());
		} else {
			alternateHandler.initNodeScope(download, nodeScopeBuilder);
		}
		
		downloadNodeScopeBuilder.properties().set(download.getProperty());
		downloadNodeScopeBuilder.getScope().getContentDeserializers().put(download.getProperty(), new NodeContentDeserializer<Void>() {
			@Override
			public Void deserialize(RepositoryNode node, NameReference contentProperty, InputStream inputStream) throws IOException {
				alternateHandler.deserialize(download, node, contentProperty, webRequest, inputStream);
				return null;
			}
		});

		// Range géré coté Alfresco
		download.getRemoteCallParameters().contentRangeStart(download.getContentRangeStart());
		download.getRemoteCallParameters().contentRangeEnd(download.getContentRangeEnd());
		
		RemoteCallParameters.execute(download.getRemoteCallParameters(), new Callable<RepositoryNode>() {
			@Override
			public RepositoryNode call() throws Exception {
				return nodeService.get(download.getNodeReference(), nodeScopeBuilder.getScope());
			}
		});
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

}
