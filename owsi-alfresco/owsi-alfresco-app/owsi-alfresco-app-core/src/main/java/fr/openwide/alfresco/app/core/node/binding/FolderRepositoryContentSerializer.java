package fr.openwide.alfresco.app.core.node.binding;

import java.io.File;
import java.io.IOException;

import fr.openwide.alfresco.app.core.node.binding.AbstractFileRepositoryContentSerializer;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

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
		String fileName = node.getProperty(propertyName, String.class);
		if (fileName == null) {
			throw new IllegalStateException("Node " + node.getNodeReference() + " doesn't have a " + propertyName);
		}
		return new File(destinationFolder, fileName);
	}

}
