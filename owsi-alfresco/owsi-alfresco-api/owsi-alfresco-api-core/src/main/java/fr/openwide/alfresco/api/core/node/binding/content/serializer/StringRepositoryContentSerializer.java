package fr.openwide.alfresco.api.core.node.binding.content.serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import fr.openwide.alfresco.api.core.node.binding.content.NodeContentDeserializer;
import fr.openwide.alfresco.api.core.node.binding.content.NodeContentSerializer;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class StringRepositoryContentSerializer implements NodeContentSerializer<String>, NodeContentDeserializer<String> {

	public static final StringRepositoryContentSerializer INSTANCE = new StringRepositoryContentSerializer(StandardCharsets.UTF_8);

	private final Charset charset;

	public StringRepositoryContentSerializer(Charset charset) {
		this.charset = charset;
	}

	@Override
	public void serialize(RepositoryNode node, NameReference contentProperty, String content, OutputStream outputStream) throws IOException {
		OutputStreamWriter wos = new OutputStreamWriter(outputStream, charset);
		wos.write(content);
		wos.flush();
	}

	@Override
	public String deserialize(RepositoryNode node, NameReference contentProperty, InputStream inputStream) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		new OutputStreamRepositoryContentDeserializer(outputStream).deserialize(node, contentProperty, inputStream);
		return new String(outputStream.toByteArray(), charset);
	}

}
