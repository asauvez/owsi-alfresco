package fr.openwide.alfresco.repository.api.node.serializer;

import java.io.FilterInputStream;
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

import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNodeVisitor;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public final class RepositoryContentSerializerUtils {

	public static final String CONTENT_TYPE = "application/zip";
	
	private static final NameReference CONTENT_IDS = NameReference.create(RepositoryContentSerializerUtils.class.getSimpleName(), "contentIds");
	private static final NameReference CONTENT_PROPERTIES = NameReference.create(RepositoryContentSerializerUtils.class.getSimpleName(), "contentProperties");
	
	private RepositoryContentSerializerUtils() {}

	public static void serializeProperties(Collection<RepositoryNode> nodes) {
		RepositoryNodeVisitor visitor = new RepositoryNodeVisitor() {
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
		
		for (RepositoryNode node : nodes) {
			node.visit(visitor);
		}
	}
	
	public static void serializeContent(Collection<RepositoryNode> nodes,
			final Map<NameReference, RepositoryContentSerializer<?>> serializersByProperties,
			final Map<Class<?>, RepositoryContentSerializer<?>> serializersByClass,
			OutputStream outputStream) throws IOException {

		final ZipOutputStream zos = new ZipOutputStream(outputStream);
		zos.setLevel(0); // sans compression

		RepositoryNodeVisitor visitor = new RepositoryNodeVisitor() {
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
		
		for (RepositoryNode node : nodes) {
			node.visit(visitor);
		}
		zos.flush();
		zos.close();
	}
	
	public static class ContentPropertyWrapper {
		public RepositoryNode node;
		public NameReference contentProperty;
	}
	
	public static void deserialize(
			Collection<RepositoryNode> nodes, 
			Map<NameReference, RepositoryContentDeserializer<?>> deserializersByProperties,
			RepositoryContentDeserializer<?> defaultDeserializer,
			InputStream inputStream) throws IOException {
		Map<Integer, ContentPropertyWrapper> wrapper = deserializeProperties(nodes);
		deserializeContent(wrapper, deserializersByProperties, defaultDeserializer, inputStream);
	}

	public static Map<Integer, ContentPropertyWrapper> deserializeProperties(Collection<RepositoryNode> nodes) {
		final Map<Integer, ContentPropertyWrapper> wrappers = new HashMap<>();
		RepositoryNodeVisitor visitor = new RepositoryNodeVisitor() {
			@Override
			@SuppressWarnings("unchecked")
			public void visit(RepositoryNode node) {
				ArrayList<Integer> contentIds = (ArrayList<Integer>) node.getExtensions().remove(CONTENT_IDS);
				if (contentIds != null) {
					List<String> contentProperties = (List<String>) node.getExtensions().remove(CONTENT_PROPERTIES);
					
					for (int i=0; i<contentIds.size(); i++) {
						ContentPropertyWrapper wrapper = new ContentPropertyWrapper();
						wrapper.node = node;
						wrapper.contentProperty = NameReference.create(contentProperties.get(i));
						wrappers.put(contentIds.get(i), wrapper);
					}
				}
			}
		};
		for (RepositoryNode node : nodes) {
			node.visit(visitor);
		}
		return wrappers;
	}
	
	public static void deserializeContent(
			Map<Integer, ContentPropertyWrapper> wrappers,
			Map<NameReference, RepositoryContentDeserializer<?>> deserializersByProperties,
			RepositoryContentDeserializer<?> defaultDeserializer,
			InputStream inputStream) throws IOException {
		ZipInputStream zis = new ZipInputStream(inputStream);
		ZipEntry zipEntry;
		while ((zipEntry = zis.getNextEntry()) != null) {
			int contentId = Integer.parseInt(zipEntry.getName());
			ContentPropertyWrapper wrapper = wrappers.get(contentId);

			RepositoryContentDeserializer<?> serializer = deserializersByProperties.get(wrapper.contentProperty);
			if (serializer == null) {
				serializer = defaultDeserializer;
			}
			Object content = serializer.deserialize(wrapper.node, wrapper.contentProperty, new FilterInputStream(zis) {
				@Override
				public void close() throws IOException {
					// on ne veut pas que le deserializer (Alfresco) ferme le flux, car c'est un flux Zip. 
					// On ne pourrait pas lire l'entrée suivante. 
				}
			});
			wrapper.node.getContents().put(wrapper.contentProperty, content);
		}
		zis.close();
	}
	
}
