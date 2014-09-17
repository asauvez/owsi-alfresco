package fr.openwide.alfresco.repository.api.node.binding;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.openwide.alfresco.repository.api.node.model.ContentPropertyWrapper;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNodeVisitor;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public final class RepositoryContentSerializationUtils {

	public static final String CONTENT_TYPE = "application/zip";
	
	private static final NameReference CONTENT_IDS = NameReference.create(RepositoryContentSerializationUtils.class.getName(), "contentIds");
	private static final NameReference CONTENT_PROPERTIES = NameReference.create(RepositoryContentSerializationUtils.class.getName(), "contentProperties");
	
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	private RepositoryContentSerializationUtils() {}

	public static void serialize(
			Object payload,
			Collection<RepositoryNode> nodes,
			final Map<NameReference, RepositoryContentSerializer<?>> serializersByProperties,
			final Map<Class<?>, RepositoryContentSerializer<?>> serializersByClass,
			OutputStream outputStream) throws IOException {

		RepositoryNodeVisitor visitor1 = new RepositoryNodeVisitor() {
			private int nextContentId = 0;
			@Override
			@SuppressWarnings("unchecked")
			public void visit(RepositoryNode node) {
				if (! node.getContents().isEmpty()) {
					node.getExtensions().put(CONTENT_IDS, new ArrayList<Integer>());
					node.getExtensions().put(CONTENT_PROPERTIES, new ArrayList<String>());
				}
				for (NameReference contentProperty : node.getContents().keySet()) {
					((List<Integer>) node.getExtensions().get(CONTENT_IDS)).add(nextContentId);
					((List<String>) node.getExtensions().get(CONTENT_PROPERTIES)).add(contentProperty.getFullName());
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
		zos.setLevel(0); // sans compression
		
		zos.putNextEntry(new ZipEntry("json"));
		objectMapper.writeValue(NonclosingStreamUtils.nonClosing(zos), payload);
		zos.closeEntry();

		RepositoryNodeVisitor visitor2 = new RepositoryNodeVisitor() {
			@Override
			@SuppressWarnings("unchecked")
			public void visit(RepositoryNode node) {
				List<Integer> contentIds = (List<Integer>) node.getExtensions().get(CONTENT_IDS);
				if (contentIds != null) {
					List<String> contentProperties = (List<String>) node.getExtensions().get(CONTENT_PROPERTIES);
					for (int i=0; i<contentIds.size(); i++) {
						int contentId = contentIds.get(i);
						NameReference contentProperty = NameReference.create(contentProperties.get(i));
						
						Object content = node.getContents().get(contentProperty);
						RepositoryContentSerializer<Object> serializer = (RepositoryContentSerializer<Object>) serializersByProperties.get(contentProperty);
						if (serializer == null) {
							for (Entry<Class<?>, RepositoryContentSerializer<?>> serializerEntry : serializersByClass.entrySet()) {
								if (serializerEntry.getKey().isInstance(content)) {
									serializer = (RepositoryContentSerializer<Object>) serializerEntry.getValue();
									break;
								}
							}
						}
						if (serializer == null) {
							throw new IllegalArgumentException(contentProperty + "/" + content.getClass() +  " has no default serializer. You should provide one.");
						}
						try {
							zos.putNextEntry(new ZipEntry(Integer.toString(contentId)));
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
		zos.close();
	}
	

	public static <P> P deserialize(
			JavaType valueType,
			NodePayloadCallback<P> payloadCallback,
			Map<NameReference, RepositoryContentDeserializer<?>> deserializersByProperties,
			RepositoryContentDeserializer<?> defaultDeserializer,
			InputStream inputStream) throws IOException {

		ZipInputStream zis = new ZipInputStream(inputStream);
		final Map<Integer, ContentPropertyWrapper> wrappers = new HashMap<>();
		
		zis.getNextEntry();
		InputStream nonClosingZis = NonclosingStreamUtils.nonClosing(zis);
		P payload = objectMapper.readValue(nonClosingZis, valueType);
		
		RepositoryNodeVisitor visitor = new RepositoryNodeVisitor() {
			@Override
			@SuppressWarnings("unchecked")
			public void visit(RepositoryNode node) {
				ArrayList<Integer> contentIds = (ArrayList<Integer>) node.getExtensions().remove(CONTENT_IDS);
				if (contentIds != null) {
					List<String> contentProperties = (List<String>) node.getExtensions().remove(CONTENT_PROPERTIES);
					
					for (int i=0; i<contentIds.size(); i++) {
						ContentPropertyWrapper wrapper = new ContentPropertyWrapper(node, NameReference.create(contentProperties.get(i)));
						wrappers.put(contentIds.get(i), wrapper);
					}
				}
			}
		};
		
		if (payloadCallback != null) {
			for (RepositoryNode node : payloadCallback.extractNodes(payload)) {
				node.visit(visitor);
			}
			payloadCallback.doWithPayload(payload, wrappers);
		}
		
		ZipEntry zipEntry;
		while ((zipEntry = zis.getNextEntry()) != null) {
			int contentId = Integer.parseInt(zipEntry.getName());
			ContentPropertyWrapper wrapper = wrappers.get(contentId);

			RepositoryContentDeserializer<?> serializer = deserializersByProperties.get(wrapper.getContentProperty());
			if (serializer == null) {
				serializer = defaultDeserializer;
			}
			Object content = serializer.deserialize(wrapper.getNode(), wrapper.getContentProperty(), nonClosingZis);
			wrapper.getNode().getContents().put(wrapper.getContentProperty(), content);
		}
		zis.close();
		
		return payload;
	}

}
