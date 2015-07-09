package fr.openwide.alfresco.api.core.node.binding.content;

import java.io.IOException;
import java.io.InputStream;

import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public interface NodeContentDeserializer<T> {

	T deserialize(RepositoryNode node, NameReference contentProperty, InputStream inputStream) throws IOException;

}
