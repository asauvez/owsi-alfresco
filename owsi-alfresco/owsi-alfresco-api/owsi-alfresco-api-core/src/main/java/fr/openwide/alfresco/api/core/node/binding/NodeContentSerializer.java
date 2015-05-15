package fr.openwide.alfresco.api.core.node.binding;

import java.io.IOException;
import java.io.OutputStream;

import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public interface NodeContentSerializer<T> {

	void serialize(RepositoryNode node, NameReference contentProperty, T content, OutputStream outputStream) throws IOException;

}
