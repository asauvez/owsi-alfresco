package fr.openwide.alfresco.app.core.node.binding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import fr.openwide.alfresco.api.core.node.binding.NodeContentDeserializer;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class OutputStreamRepositoryContentDeserializer implements NodeContentDeserializer<Void> {

	private final OutputStream outputStream;
	
	public OutputStreamRepositoryContentDeserializer(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	@Override
	public Void deserialize(RepositoryNode node, NameReference contentProperty, InputStream inputStream) throws IOException {
		
		IOUtils.copy(inputStream, outputStream);
		
		return null;
	}

}