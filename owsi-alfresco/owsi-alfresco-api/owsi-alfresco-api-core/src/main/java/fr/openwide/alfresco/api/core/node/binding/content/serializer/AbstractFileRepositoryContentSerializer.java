package fr.openwide.alfresco.api.core.node.binding.content.serializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import fr.openwide.alfresco.api.core.node.binding.content.NodeContentDeserializer;
import fr.openwide.alfresco.api.core.node.binding.content.NodeContentSerializer;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public abstract class AbstractFileRepositoryContentSerializer 
		implements NodeContentSerializer<File>, NodeContentDeserializer<File> {

	@Override
	public void serialize(RepositoryNode node, NameReference contentProperty, File content, OutputStream outputStream) throws IOException {
		try (InputStream inputStream = new FileInputStream(content)) {
			new OutputStreamRepositoryContentDeserializer(outputStream).deserialize(node, contentProperty, inputStream);
		}
	}

	@Override
	public File deserialize(RepositoryNode node, NameReference contentProperty, InputStream inputStream) throws IOException {
		File file = getFile(node);
		if (file.exists() && file.length() > 0) {
			throw new IllegalStateException("The file " + file.getAbsolutePath() + " already exists.");
		}
		try (OutputStream outputStream = new FileOutputStream(file)) {
			new OutputStreamRepositoryContentDeserializer(outputStream).deserialize(node, contentProperty, inputStream);
		}
		return file;
	}

	protected abstract File getFile(RepositoryNode node) throws IOException;

}
