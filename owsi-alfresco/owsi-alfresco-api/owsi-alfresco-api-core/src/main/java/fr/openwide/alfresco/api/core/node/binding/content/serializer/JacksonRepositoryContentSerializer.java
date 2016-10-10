package fr.openwide.alfresco.api.core.node.binding.content.serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.openwide.alfresco.api.core.node.binding.content.NodeContentDeserializer;
import fr.openwide.alfresco.api.core.node.binding.content.NodeContentSerializer;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class JacksonRepositoryContentSerializer<T> implements NodeContentSerializer<T>, NodeContentDeserializer<T> {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final Class<T> clazz;

	public JacksonRepositoryContentSerializer(Class<T> clazz) {
		this.clazz = clazz;
	}
	
	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	@Override
	public void serialize(RepositoryNode node, NameReference contentProperty, T content, OutputStream outputStream) throws IOException {
		objectMapper.writeValue(outputStream, content);
	}

	@Override
	public T deserialize(RepositoryNode node, NameReference contentProperty, InputStream inputStream) throws IOException {
		return objectMapper.readValue(inputStream, clazz);
	}

}
