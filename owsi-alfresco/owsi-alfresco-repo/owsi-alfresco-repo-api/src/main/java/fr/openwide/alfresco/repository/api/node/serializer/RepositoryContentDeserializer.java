package fr.openwide.alfresco.repository.api.node.serializer;

import java.io.IOException;
import java.io.InputStream;

import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public interface RepositoryContentDeserializer<T> {

	T deserialize(RepositoryNode node, NameReference contentProperty, InputStream inputStream) throws IOException;

}
