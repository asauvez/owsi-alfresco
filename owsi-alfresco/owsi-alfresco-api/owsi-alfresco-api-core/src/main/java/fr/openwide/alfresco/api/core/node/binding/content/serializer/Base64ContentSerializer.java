package fr.openwide.alfresco.api.core.node.binding.content.serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import fr.openwide.alfresco.api.core.node.binding.content.NodeContentDeserializer;
import fr.openwide.alfresco.api.core.node.model.RepositoryContentData;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class Base64ContentSerializer implements NodeContentDeserializer<String> {

	private boolean addMimePrefix;

	public Base64ContentSerializer(boolean addMimePrefix) {
		this.addMimePrefix = addMimePrefix;
	}

	@Override
	public String deserialize(RepositoryNode node, NameReference contentProperty, InputStream inputStream) throws IOException {
		RepositoryContentData contentData =  (RepositoryContentData) node.getProperty(contentProperty);
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(contentData.getSize().intValue());
		new OutputStreamRepositoryContentDeserializer(outputStream).deserialize(node, contentProperty, inputStream);
		
		return ((addMimePrefix) ? "data:" + contentData.getMimetype() + ";base64," : "") 
			+ Base64.getUrlEncoder().encodeToString(outputStream.toByteArray());
	}

}
