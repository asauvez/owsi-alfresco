package fr.openwide.alfresco.api.core.node.binding.content.serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import fr.openwide.alfresco.api.core.node.binding.content.NodeContentDeserializer;
import fr.openwide.alfresco.api.core.node.binding.content.NodeContentSerializer;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class ByteArrayRepositoryContentSerializer 
		implements NodeContentSerializer<byte[]>, NodeContentDeserializer<byte[]> {

	public static final ByteArrayRepositoryContentSerializer INSTANCE = new ByteArrayRepositoryContentSerializer();

	protected ByteArrayRepositoryContentSerializer() {}

	@Override
	public void serialize(RepositoryNode node, NameReference contentProperty, byte[] content, OutputStream outputStream) throws IOException {
		outputStream.write(content);
	}

	@Override
	public byte[] deserialize(RepositoryNode node, NameReference contentProperty, InputStream inputStream) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		new OutputStreamRepositoryContentDeserializer(out).deserialize(node, contentProperty, inputStream);
		return out.toByteArray();
	}

}
