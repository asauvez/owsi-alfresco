package fr.openwide.alfresco.api.core.node.binding.content.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import fr.openwide.alfresco.api.core.node.binding.content.NodeContentSerializer;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class ReaderRepositoryContentSerializer implements NodeContentSerializer<Reader> {

	public static final ReaderRepositoryContentSerializer INSTANCE = new ReaderRepositoryContentSerializer(StandardCharsets.UTF_8);
	
	private final Charset charset;

	public ReaderRepositoryContentSerializer(Charset charset) {
		this.charset = charset;
	}

	@Override
	public void serialize(RepositoryNode node, NameReference contentProperty, Reader reader, OutputStream outputStream)
			throws IOException {
		OutputStreamWriter writer = new OutputStreamWriter(outputStream, charset);
		char[] buffer = new char[4096];
		int n = 0;
		while ((n = reader.read(buffer)) != -1) {
			writer.write(buffer, 0, n);
		}
		writer.flush();
	}

}
