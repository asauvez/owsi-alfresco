package fr.openwide.alfresco.api.core.node.binding.content.serializer;

import java.io.File;
import java.io.IOException;

import fr.openwide.alfresco.api.core.node.model.RepositoryNode;

public class SingleFileRepositoryContentSerializer extends AbstractFileRepositoryContentSerializer {

	private final File destinationFile;

	public SingleFileRepositoryContentSerializer(File destinationFile) {
		this.destinationFile = destinationFile;
	}

	@Override
	protected File getFile(RepositoryNode node) throws IOException {
		return destinationFile;
	}

}
