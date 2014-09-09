package fr.openwide.alfresco.app.core.node.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.node.serializer.RepositoryContentSerializer;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class ReaderRepositoryContentSerializer implements RepositoryContentSerializer<Reader> {

	public static final ReaderRepositoryContentSerializer INSTANCE = new ReaderRepositoryContentSerializer(StandardCharsets.UTF_8);
	
	private final Charset charset;

	public ReaderRepositoryContentSerializer(Charset charset) {
		this.charset = charset;
	}

	@Override
	public void serialize(RepositoryNode node, NameReference contentProperty, Reader content, OutputStream outputStream)
			throws IOException {
		try (Reader reader = content) {;
			IOUtils.copy(reader, outputStream, charset);
		}
	}

}
