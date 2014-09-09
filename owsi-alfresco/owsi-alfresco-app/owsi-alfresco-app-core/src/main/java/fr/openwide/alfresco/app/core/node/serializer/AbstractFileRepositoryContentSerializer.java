package fr.openwide.alfresco.app.core.node.serializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.node.serializer.RepositoryContentDeserializer;
import fr.openwide.alfresco.repository.api.node.serializer.RepositoryContentSerializer;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public abstract class AbstractFileRepositoryContentSerializer 
	implements RepositoryContentSerializer<File>, RepositoryContentDeserializer<File> {

	@Override
	public void serialize(RepositoryNode node, NameReference contentProperty, File content, OutputStream outputStream) throws IOException {
		try (InputStream inputStream = new FileInputStream(content)) {
			IOUtils.copy(inputStream, outputStream);
		}
	}

	@Override
	public File deserialize(RepositoryNode node, NameReference contentProperty, InputStream inputStream) throws IOException {
		File file = getFile(node);
		try (OutputStream outputStream = new FileOutputStream(file)) {
			IOUtils.copy(inputStream, outputStream);
		}
		return file;
	}

	protected abstract File getFile(RepositoryNode node) throws IOException;
}
