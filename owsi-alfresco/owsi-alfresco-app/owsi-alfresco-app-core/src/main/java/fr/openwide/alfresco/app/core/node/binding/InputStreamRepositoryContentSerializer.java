package fr.openwide.alfresco.app.core.node.binding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import fr.openwide.alfresco.repository.api.node.binding.RepositoryContentSerializer;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class InputStreamRepositoryContentSerializer implements RepositoryContentSerializer<InputStream> {

	public static final InputStreamRepositoryContentSerializer INSTANCE = new InputStreamRepositoryContentSerializer();

	protected InputStreamRepositoryContentSerializer() {}

	@Override
	public void serialize(RepositoryNode node, NameReference contentProperty, InputStream content, OutputStream outputStream)
			throws IOException {
		try (InputStream inputStream = content) {;
			IOUtils.copy(inputStream, outputStream);
		}
	}

}
