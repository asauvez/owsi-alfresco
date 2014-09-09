package fr.openwide.alfresco.app.core.node.serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.node.serializer.RepositoryContentDeserializer;
import fr.openwide.alfresco.repository.api.node.serializer.RepositoryContentSerializer;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class StringRepositoryContentSerializer 
		implements RepositoryContentSerializer<String>, RepositoryContentDeserializer<String> {

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
		return IOUtils.toString(inputStream, charset);
	}

}
