package fr.openwide.alfresco.api.core.node.binding.content.serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import fr.openwide.alfresco.api.core.node.binding.content.NodeContentDeserializer;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class OutputStreamRepositoryContentDeserializer implements NodeContentDeserializer<Void> {

	private final OutputStream outputStream;
	
	public OutputStreamRepositoryContentDeserializer(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	@Override
	public Void deserialize(RepositoryNode node, NameReference contentProperty, InputStream inputStream) throws IOException {
		byte[] buffer = new byte[4096];
		int n = 0;
		while ((n = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, n);
		}
		return null;
	}

}