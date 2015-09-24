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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
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

	private static final String JSON_ZIP_ENTRY_NAME = "json";

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

	@SuppressWarnings("unchecked")
	public void serialize(
			Object payload,
			Collection<RepositoryNode> nodes,
			final NodeContentSerializationParameters parameters,
			OutputStream outputStream) throws IOException {

		// Affecte des ID à chaque content
		final List<RepositoryNode> allNodes = new ArrayList<>();
		RepositoryVisitor<RepositoryNode> visitor1 = new RepositoryVisitor<RepositoryNode>() {
			private int nextContentId = 0;
			@Override
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
				allNodes.add(node);
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
		
		// Ecrit le JSon
		RemoteCallPayload<Object> remoteCallPayload = new RemoteCallPayload<>();
		remoteCallPayload.setPayload(payload);
		remoteCallPayload.setRemoteCallParameters(remoteCallParameters);
		
		zos.putNextEntry(new ZipEntry(JSON_ZIP_ENTRY_NAME));
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Serializing payload: {}", objectMapper.writeValueAsString(remoteCallPayload));
		}
		objectMapper.writeValue(NonClosingStreamUtils.nonClosing(zos), remoteCallPayload);
		zos.closeEntry();

		// Ecrit chaque contenu
		for (RepositoryNode node : allNodes) {
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
		zos.flush();
	}

	public <P> RemoteCallPayload<P> deserialize(
			JavaType valueType,
			NodePayloadCallback<P> payloadCallback,
			NodeContentDeserializationParameters parameters,
			InputStream inputStream) throws IOException {

		final ZipIterator zipIterator = new ZipIterator(inputStream);

		// On lit le premier fichier qui contient le JSON
		ZipEntry jsonEntry = zipIterator.getCurrentEntry();
		if (jsonEntry == null) {
			throw new IllegalStateException("Input stream is empty");
		}
		if (! JSON_ZIP_ENTRY_NAME.equals(jsonEntry.getName())) {
			throw new IllegalStateException("Input stream don't start with a json entry but with " + jsonEntry.getName());
		}
		
		JavaType remoteCallPayloadType = objectMapper.getTypeFactory().constructSimpleType(RemoteCallPayload.class, 
				new JavaType[] { valueType  });
		RemoteCallPayload<P> remoteCallPayload = objectMapper.readValue(zipIterator.getInputStream(), remoteCallPayloadType);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Deserializing payload: {}", objectMapper.writeValueAsString(remoteCallPayload));
		}
		zipIterator.next();

		final Map<Integer, ContentPropertyWrapper> wrappers = new LinkedHashMap<>();
		if (payloadCallback != null) {
			// On construit la map des wrappers
			RepositoryVisitor<RepositoryNode> visitor = new RepositoryVisitor<RepositoryNode>() {
				@Override
				@SuppressWarnings("unchecked")
				public void visit(RepositoryNode node) {
					ArrayList<Integer> contentIds = (ArrayList<Integer>) node.getExtensions().remove(CONTENT_IDS);
					if (contentIds != null) {
						List<String> contentProperties = (List<String>) node.getExtensions().remove(CONTENT_PROPERTIES);
						
						for (int i=0; i<contentIds.size(); i++) {
							Integer contentId = contentIds.get(i);
							NameReference property = NameReference.create(contentProperties.get(i));
							
							push(property);
							ContentPropertyWrapper wrapper = new ContentPropertyWrapper(node, property, getCurrentPath(), 
									contentId, zipIterator);
							pop(property);
							wrappers.put(contentId, wrapper);
						}
					}
				}
			};
			for (RepositoryNode node : payloadCallback.extractNodes(remoteCallPayload.getPayload())) {
				node.visit(visitor);
			}
			
			// On appel le service s'il y en a un
			payloadCallback.doWithPayload(remoteCallPayload, wrappers.values());
		}
		
		// On lit le reste des fichiers qui correspondent aux différents contents
		while (zipIterator.hasNext()) {
			int contentId = Integer.parseInt(zipIterator.getCurrentEntry().getName());
			ContentPropertyWrapper wrapper = wrappers.get(contentId);

			NodeContentDeserializer<?> deserializer = (parameters != null) ? parameters.getDeserializersByPath().get(wrapper.getPath()) : null;
			if (deserializer == null) {
				deserializer = defaultDeserializer;
			}
			Object content = deserializer.deserialize(wrapper.getNode(), wrapper.getContentProperty(), zipIterator.getInputStream());
			wrapper.getNode().getContents().put(wrapper.getContentProperty(), content);
			
			zipIterator.next();
		}
		zipIterator.closeLastEntry();
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
