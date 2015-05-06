package fr.openwide.alfresco.app.core.node.binding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import fr.openwide.alfresco.repository.api.node.binding.NodeContentDeserializer;
import fr.openwide.alfresco.repository.api.node.binding.NodeContentSerializer;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public abstract class AbstractFileRepositoryContentSerializer 
		implements NodeContentSerializer<File>, NodeContentDeserializer<File> {

	@Override
	public void serialize(RepositoryNode node, NameReference contentProperty, File content, OutputStream outputStream) throws IOException {
		try (InputStream inputStream = new FileInputStream(content)) {
			IOUtils.copy(inputStream, outputStream);
		}
	}

	@Override
	public File deserialize(RepositoryNode node, NameReference contentProperty, InputStream inputStream) throws IOException {
		File file = getFile(node);
		if (file.exists()) {
			throw new IllegalStateException("The file " + file.getAbsolutePath() + " already exists.");
		}
		try (OutputStream outputStream = new FileOutputStream(file)) {
			IOUtils.copy(inputStream, outputStream);
		}
		return file;
	}

	protected abstract File getFile(RepositoryNode node) throws IOException;

}
