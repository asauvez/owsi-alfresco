package fr.openwide.alfresco.api.core.node.binding.content;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.openwide.alfresco.api.core.node.binding.RemoteCallPayload;
import fr.openwide.alfresco.api.core.node.binding.content.serializer.ByteArrayRepositoryContentSerializer;
import fr.openwide.alfresco.api.core.node.binding.content.serializer.InputStreamRepositoryContentSerializer;
import fr.openwide.alfresco.api.core.node.binding.content.serializer.ReaderRepositoryContentSerializer;
import fr.openwide.alfresco.api.core.node.binding.content.serializer.StringRepositoryContentSerializer;
import fr.openwide.alfresco.api.core.node.binding.content.serializer.TempFileRepositoryContentSerializer;
import fr.openwide.alfresco.api.core.node.model.ContentPropertyWrapper;
import fr.openwide.alfresco.api.core.node.model.RemoteCallParameters;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.node.model.RepositoryVisitor;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class NodeContentSerializationComponent {

	private static final Logger LOGGER = LoggerFactory.getLogger(NodeContentSerializationComponent.class);
	
	public static final String CONTENT_TYPE = "application/zip";

	private static final NameReference CONTENT_IDS = NameReference.create(NodeContentSerializationComponent.class.getSimpleName(), "contentIds");
	private static final NameReference CONTENT_PROPERTIES = NameReference.create(NodeContentSerializationComponent.class.getSimpleName(), "contentProperties");

	private final ObjectMapper objectMapper;
	private final Map<Class<?>, NodeContentSerializer<?>> serializersByClass;
	private final NodeContentDeserializer<?> defaultDeserializer;

	public NodeContentSerializationComponent(
			ObjectMapper objectMapper, 
			Map<Class<?>, NodeContentSerializer<?>> serializersByClass,
			NodeContentDeserializer<?> defaultDeserializer) {
		this.objectMapper = objectMapper;
		this.serializersByClass = serializersByClass;
		this.defaultDeserializer = defaultDeserializer;
	}

	public void serialize(
			Object payload,
			Collection<RepositoryNode> nodes,
			final NodeContentSerializationParameters parameters,
			OutputStream outputStream) throws IOException {

		RepositoryVisitor<RepositoryNode> visitor1 = new RepositoryVisitor<RepositoryNode>() {
			private int nextContentId = 0;
			@Override
			@SuppressWarnings("unchecked")
			public void visit(RepositoryNode node) {
				if (! node.getContents().isEmpty()) {
					node.getExtensions().put(CONTENT_IDS, new ArrayList<Integer>());
					node.getExtensions().put(CONTENT_PROPERTIES, new ArrayList<String>());
				}
				for (NameReference contentProperty : node.getContents().keySet()) {
					((List<Integer>) node.getExtension(CONTENT_IDS)).add(nextContentId);
					((List<String>) node.getExtension(CONTENT_PROPERTIES)).add(contentProperty.getFullName());
					nextContentId ++;
				}
			}
		};
		
		if (nodes != null) {
			for (RepositoryNode node : nodes) {
				node.visit(visitor1);
			}
		}

		final ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(outputStream));
		RemoteCallParameters remoteCallParameters = RemoteCallParameters.currentParameters();
		zos.setLevel(remoteCallParameters.getCompressionLevel());
		
		RemoteCallPayload<Object> remoteCallPayload = new RemoteCallPayload<>();
		remoteCallPayload.setPayload(payload);
		remoteCallPayload.setRemoteCallParameters(remoteCallParameters);
		
		zos.putNextEntry(new ZipEntry("json"));
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Serializing payload: {}", objectMapper.writeValueAsString(remoteCallPayload));
		}
		objectMapper.writeValue(NonClosingStreamUtils.nonClosing(zos), remoteCallPayload);
		zos.closeEntry();

		RepositoryVisitor<RepositoryNode> visitor2 = new RepositoryVisitor<RepositoryNode>() {
			@Override
			@SuppressWarnings("unchecked")
			public void visit(RepositoryNode node) {
				List<Integer> contentIds = (List<Integer>) node.getExtension(CONTENT_IDS);
				if (contentIds != null) {
					List<String> contentProperties = (List<String>) node.getExtension(CONTENT_PROPERTIES);
					for (int i=0; i<contentIds.size(); i++) {
						int contentId = contentIds.get(i);
						NameReference contentProperty = NameReference.create(contentProperties.get(i));
						
						Object content = node.getContents().get(contentProperty);
						NodeContentSerializer<Object> serializer = (parameters != null) 
								? (NodeContentSerializer<Object>) parameters.getSerializersByProperties().get(contentProperty)
								: null;
						if (serializer == null) {
							for (Entry<Class<?>, NodeContentSerializer<?>> serializerEntry : serializersByClass.entrySet()) {
								if (serializerEntry.getKey().isInstance(content)) {
									serializer = (NodeContentSerializer<Object>) serializerEntry.getValue();
									break;
								}
							}
						}
						if (serializer == null) {
							throw new IllegalArgumentException(contentProperty + "/" + content.getClass() +  " has no default serializer. You should provide one.");
						}
						try {
							zos.putNextEntry(new ZipEntry(Integer.toString(contentId)));
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("Serializing content property '{}' for node: {}", contentProperty, node.getNodeReference());
							}
							serializer.serialize(node, contentProperty, content, zos);
							zos.closeEntry();
						} catch (IOException e) {
							throw new IllegalStateException(e);
						}
					}
				}
			}
		};
		
		if (nodes != null) {
			for (RepositoryNode node : nodes) {
				node.visit(visitor2);
			}
		}
		zos.flush();
	}

	public <P> RemoteCallPayload<P> deserialize(
			JavaType valueType,
			NodePayloadCallback<P> payloadCallback,
			NodeContentDeserializationParameters parameters,
			InputStream inputStream) throws IOException {

		ZipInputStream zis = new ZipInputStream(inputStream);
		final Map<Integer, ContentPropertyWrapper> wrappers = new HashMap<>();
		
		zis.getNextEntry();
		InputStream nonClosingZis = NonClosingStreamUtils.nonClosing(zis);
		
		
		JavaType remoteCallPayloadType = objectMapper.getTypeFactory().constructSimpleType(RemoteCallPayload.class, 
				new JavaType[] { valueType  });
		RemoteCallPayload<P> remoteCallPayload = objectMapper.readValue(nonClosingZis, remoteCallPayloadType);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Deserializing payload: {}", objectMapper.writeValueAsString(remoteCallPayload));
		}

		RepositoryVisitor<RepositoryNode> visitor = new RepositoryVisitor<RepositoryNode>() {
			@Override
			@SuppressWarnings("unchecked")
			public void visit(RepositoryNode node) {
				ArrayList<Integer> contentIds = (ArrayList<Integer>) node.getExtensions().remove(CONTENT_IDS);
				if (contentIds != null) {
					List<String> contentProperties = (List<String>) node.getExtensions().remove(CONTENT_PROPERTIES);
					
					for (int i=0; i<contentIds.size(); i++) {
						NameReference property = NameReference.create(contentProperties.get(i));
						push(property);
						ContentPropertyWrapper wrapper = new ContentPropertyWrapper(node, property, getCurrentPath());
						pop(property);
						wrappers.put(contentIds.get(i), wrapper);
					}
				}
			}
		};
		
		if (payloadCallback != null) {
			for (RepositoryNode node : payloadCallback.extractNodes(remoteCallPayload.getPayload())) {
				node.visit(visitor);
			}
			payloadCallback.doWithPayload(remoteCallPayload, wrappers);
		}
		
		ZipEntry zipEntry;
		while ((zipEntry = zis.getNextEntry()) != null) {
			int contentId = Integer.parseInt(zipEntry.getName());
			ContentPropertyWrapper wrapper = wrappers.get(contentId);

			NodeContentDeserializer<?> deserializer = (parameters != null) ? parameters.getDeserializersByPath().get(wrapper.getPath()) : null;
			if (deserializer == null) {
				deserializer = defaultDeserializer;
			}
			Object content = deserializer.deserialize(wrapper.getNode(), wrapper.getContentProperty(), nonClosingZis);
			wrapper.getNode().getContents().put(wrapper.getContentProperty(), content);
		}
		return remoteCallPayload;
	}

	public static Map<Class<?>, NodeContentSerializer<?>> getDefaultSerializersByClass() {
		Map<Class<?>, NodeContentSerializer<?>> serializersByClass = new HashMap<>();
		serializersByClass.put(String.class, StringRepositoryContentSerializer.INSTANCE);
		serializersByClass.put(byte[].class, ByteArrayRepositoryContentSerializer.INSTANCE);
		serializersByClass.put(File.class, TempFileRepositoryContentSerializer.INSTANCE);
		serializersByClass.put(InputStream.class, InputStreamRepositoryContentSerializer.INSTANCE);
		serializersByClass.put(Reader.class, ReaderRepositoryContentSerializer.INSTANCE);
		return serializersByClass;
	}
}
