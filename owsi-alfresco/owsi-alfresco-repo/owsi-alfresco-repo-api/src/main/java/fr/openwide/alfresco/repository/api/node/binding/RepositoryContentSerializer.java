package fr.openwide.alfresco.repository.api.node.binding;

import java.io.IOException;
import java.io.OutputStream;

import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public interface RepositoryContentSerializer<T> {

	void serialize(RepositoryNode node, NameReference contentProperty, T content, OutputStream outputStream) throws IOException;

}
