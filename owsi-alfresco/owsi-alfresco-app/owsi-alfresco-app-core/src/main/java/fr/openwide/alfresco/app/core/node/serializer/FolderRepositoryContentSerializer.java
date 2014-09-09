package fr.openwide.alfresco.app.core.node.serializer;

import java.io.File;
import java.io.IOException;

import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class FolderRepositoryContentSerializer extends AbstractFileRepositoryContentSerializer {

	private final File destinationFolder;
	private final NameReference propertyName;

	public FolderRepositoryContentSerializer(File destinationFolder) {
		this(destinationFolder, NameReference.create("cm", "name"));
	}
	public FolderRepositoryContentSerializer(File destinationFolder, NameReference propertyName) {
		this.destinationFolder = destinationFolder;
		this.propertyName = propertyName;
	}

	@Override
	protected File getFile(RepositoryNode node) throws IOException {
		String fileName = (String) node.getProperties().get(propertyName);
		if (fileName == null) {
			throw new IllegalStateException("Node " + node.getNodeReference() + " doesn't have a " + propertyName);
		}
		return new File(destinationFolder, fileName);
	}

}
