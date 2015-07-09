package fr.openwide.alfresco.api.core.node.binding.content.serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import fr.openwide.alfresco.api.core.node.binding.content.NodeContentSerializer;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class InputStreamRepositoryContentSerializer implements NodeContentSerializer<InputStream> {

	public static final InputStreamRepositoryContentSerializer INSTANCE = new InputStreamRepositoryContentSerializer();

	protected InputStreamRepositoryContentSerializer() {}

	@Override
	public void serialize(RepositoryNode node, NameReference contentProperty, InputStream inputStream, OutputStream outputStream)
			throws IOException {
		new OutputStreamRepositoryContentDeserializer(outputStream).deserialize(node, contentProperty, inputStream);
	}

}
