package fr.openwide.alfresco.app.core.node.binding;

import java.io.File;
import java.io.IOException;

import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;

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
