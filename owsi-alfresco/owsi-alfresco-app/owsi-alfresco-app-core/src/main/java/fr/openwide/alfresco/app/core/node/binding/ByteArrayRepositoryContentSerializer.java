package fr.openwide.alfresco.app.core.node.binding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import fr.openwide.alfresco.repository.api.node.binding.RepositoryContentDeserializer;
import fr.openwide.alfresco.repository.api.node.binding.RepositoryContentSerializer;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class ByteArrayRepositoryContentSerializer 
		implements RepositoryContentSerializer<byte[]>, RepositoryContentDeserializer<byte[]> {

	public static final ByteArrayRepositoryContentSerializer INSTANCE = new ByteArrayRepositoryContentSerializer();

	protected ByteArrayRepositoryContentSerializer() {}

	@Override
	public void serialize(RepositoryNode node, NameReference contentProperty, byte[] content, OutputStream outputStream) throws IOException {
		outputStream.write(content);
	}

	@Override
	public byte[] deserialize(RepositoryNode node, NameReference contentProperty, InputStream inputStream) throws IOException {
		return IOUtils.toByteArray(inputStream);
	}

}
