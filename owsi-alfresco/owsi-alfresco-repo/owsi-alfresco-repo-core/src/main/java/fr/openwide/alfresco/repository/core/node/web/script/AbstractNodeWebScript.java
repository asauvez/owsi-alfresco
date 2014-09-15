package fr.openwide.alfresco.repository.core.node.web.script;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.service.cmr.repository.ContentReader;
import org.apache.commons.io.IOUtils;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.node.serializer.RepositoryContentDeserializer;
import fr.openwide.alfresco.repository.api.node.serializer.RepositoryContentSerializer;
import fr.openwide.alfresco.repository.api.node.serializer.RepositoryContentSerializerUtils;
import fr.openwide.alfresco.repository.api.node.serializer.RepositoryContentSerializerUtils.ContentPropertyWrapper;
import fr.openwide.alfresco.repository.api.node.service.NodeRemoteService;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.api.remote.model.endpoint.RestEndpoint;
import fr.openwide.alfresco.repository.remote.framework.web.script.AbstractParameterRemoteWebScript;

public abstract class AbstractNodeWebScript<R, P> extends AbstractParameterRemoteWebScript<R, P> {

	protected NodeRemoteService nodeService;

	protected abstract R execute(P payload);
	
	protected Collection<RepositoryNode> getUploadedNodes(@SuppressWarnings("unused") P payload) {
		return null;
	}
	protected Collection<RepositoryNode> getDownloadedNodes(@SuppressWarnings("unused") R response) {
		return null;
	}

	
	@Override
	protected R executeImpl(P payload, WebScriptRequest req) {
		Collection<RepositoryNode> uploadedNodes = getUploadedNodes(payload);
		
		Map<Integer, ContentPropertyWrapper> wrappers = (uploadedNodes != null) ? deserializeContentProperties(uploadedNodes) : null;
		
		R result = execute(payload);
		
		if (wrappers != null) {
			// Ecrit les contents uploadés dans Alfresco
			deserializeContentData(wrappers, req);
		}

		return result;
	}
	
	@Override
	protected void handleResult(WebScriptResponse res, R resValue) throws IOException {
		Collection<RepositoryNode> downloadedNodes = getDownloadedNodes(resValue);
		if (downloadedNodes == null) {
			super.handleResult(res, resValue);
		} else {
			serializeProperties(downloadedNodes);
			
			String valueHeader = objectMapper.writeValueAsString(resValue);
			res.setHeader(RestEndpoint.HEADER_MESSAGE_CONTENT, URLEncoder.encode(valueHeader, "UTF-8"));
			
			serializeContent(downloadedNodes, res);
		}
	}
	
	
	
	
	
	public static class ContentCallback {
		@SuppressWarnings("unused")
		public void doWithInputStream(NameReference contentProperty, InputStream inputStream) {}
	}
	
	private void serializeProperties(Collection<RepositoryNode> nodes) {
		RepositoryContentSerializerUtils.serializeProperties(nodes);
	}
	
	private void serializeContent(Collection<RepositoryNode> nodes, WebScriptResponse res) {
		res.setContentType(RepositoryContentSerializerUtils.CONTENT_TYPE);
		
		Map<Class<?>, RepositoryContentSerializer<?>> serializersByClass = new HashMap<>();
		serializersByClass.put(ContentReader.class, new RepositoryContentSerializer<ContentReader>() {
			@Override
			public void serialize(RepositoryNode node, NameReference contentProperty, ContentReader ContentReader, OutputStream outputStream) throws IOException {
				try (InputStream inputStream = ContentReader.getContentInputStream()) {
					IOUtils.copy(inputStream, outputStream);
				}
			}
		});
		
		try {
			RepositoryContentSerializerUtils.serializeContent(
					nodes, 
					new HashMap<NameReference, RepositoryContentSerializer<?>>(), 
					serializersByClass, 
					res.getOutputStream());
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	/** Initialise la map de content.
	 *  Appeler avant l'appel au service */
	private Map<Integer, ContentPropertyWrapper> deserializeContentProperties(Collection<RepositoryNode> nodes) {
		Map<Integer, ContentPropertyWrapper> wrappers = RepositoryContentSerializerUtils.deserializeProperties(nodes);
		for (ContentPropertyWrapper wrapper : wrappers.values()) {
			wrapper.node.getContents().put(wrapper.contentProperty, new ContentCallback());
		}
		return wrappers;
	}
	
	/** Lit le zip de content et appelle les callbacks.
	 *  Appeler après l'appel au service */
	private void deserializeContentData(Map<Integer, ContentPropertyWrapper> wrappers, WebScriptRequest req) {
		try {
			RepositoryContentDeserializer<Void> defaultDeserializer = new RepositoryContentDeserializer<Void>() {
				@Override
				public Void deserialize(RepositoryNode node, NameReference contentProperty, InputStream inputStream) throws IOException {
					ContentCallback callback = (ContentCallback) node.getContents().get(contentProperty);
					callback.doWithInputStream(contentProperty, inputStream);
					return null;
				}
			};

			RepositoryContentSerializerUtils.deserializeContent(wrappers, 
					new HashMap<NameReference, RepositoryContentDeserializer<?>>(),  
					defaultDeserializer, 
					req.getContent().getInputStream());
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}


	public void setNodeService(NodeRemoteService nodeService) {
		this.nodeService = nodeService;
	}

}
