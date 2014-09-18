package fr.openwide.alfresco.app.core.node.binding;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.input.ReaderInputStream;

import fr.openwide.alfresco.repository.api.node.binding.RepositoryContentDeserializer;
import fr.openwide.alfresco.repository.api.node.binding.RepositoryContentSerializer;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class ReaderRepositoryContentSerializer 
		implements RepositoryContentSerializer<Reader>, RepositoryContentDeserializer<Reader> {

	public static final ReaderRepositoryContentSerializer INSTANCE = new ReaderRepositoryContentSerializer(StandardCharsets.UTF_8);
	
	private final Charset charset;

	public ReaderRepositoryContentSerializer(Charset charset) {
		this.charset = charset;
	}

	@Override
	public void serialize(RepositoryNode node, NameReference contentProperty, Reader content, OutputStream outputStream)
			throws IOException {
		InputStreamRepositoryContentSerializer.INSTANCE.serialize(node, contentProperty, 
				new ReaderInputStream(content, charset), 
				outputStream);
	}
	
	@Override
	public Reader deserialize(RepositoryNode node, NameReference contentProperty, InputStream inputStream) throws IOException {
		return new InputStreamReader(
				InputStreamRepositoryContentSerializer.INSTANCE.deserialize(node, contentProperty, inputStream), 
				charset);
	}

}
