package fr.openwide.alfresco.app.core.node.binding;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import fr.openwide.alfresco.repository.api.node.binding.NodeContentSerializer;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class MultipartFileRepositoryContentSerializer implements NodeContentSerializer<MultipartFile> {

	public static final MultipartFileRepositoryContentSerializer INSTANCE = new MultipartFileRepositoryContentSerializer();

	protected MultipartFileRepositoryContentSerializer() {}

	@Override
	public void serialize(RepositoryNode node, NameReference contentProperty, MultipartFile content, OutputStream outputStream) throws IOException {
		IOUtils.copy(content.getInputStream(), outputStream);
	}

}
