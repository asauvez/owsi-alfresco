package fr.openwide.alfresco.app.core.node.binding;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.input.ReaderInputStream;

import fr.openwide.alfresco.api.core.node.binding.NodeContentSerializer;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class ReaderRepositoryContentSerializer implements NodeContentSerializer<Reader> {

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

}
