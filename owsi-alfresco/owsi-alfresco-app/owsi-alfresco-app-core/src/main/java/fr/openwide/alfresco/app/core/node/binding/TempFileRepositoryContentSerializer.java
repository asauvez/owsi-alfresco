package fr.openwide.alfresco.app.core.node.binding;

import java.io.File;
import java.io.IOException;

import fr.openwide.alfresco.app.core.node.binding.AbstractFileRepositoryContentSerializer;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;

public class TempFileRepositoryContentSerializer extends AbstractFileRepositoryContentSerializer {

	public static final TempFileRepositoryContentSerializer INSTANCE = new TempFileRepositoryContentSerializer();

	protected TempFileRepositoryContentSerializer() {}

	@Override
	protected File getFile(RepositoryNode node) throws IOException {
		return File.createTempFile(TempFileRepositoryContentSerializer.class.getSimpleName(), null);
	}

}
