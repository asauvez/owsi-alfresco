package fr.openwide.alfresco.app.core.node.binding;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import fr.openwide.alfresco.api.core.node.binding.NodeContentDeserializer;
import fr.openwide.alfresco.api.core.node.model.RepositoryContentData;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class HttpServletResponseRepositoryContentDeserializer implements NodeContentDeserializer<Void> {

	private final HttpServletResponse response;
	private final String fileName;
	private final NameReference nameProperty;
	private final boolean inline;
	
	public HttpServletResponseRepositoryContentDeserializer(HttpServletResponse response, String fileName, boolean inline) {
		this(response, fileName, null, inline);
	}
	public HttpServletResponseRepositoryContentDeserializer(HttpServletResponse response, NameReference nameProperty, boolean inline) {
		this(response, null, nameProperty, inline);
	}
	protected HttpServletResponseRepositoryContentDeserializer(HttpServletResponse response, 
			String fileName, NameReference nameProperty, boolean inline) {
		this.response = response;
		this.fileName = fileName;
		this.nameProperty = nameProperty;
		this.inline = inline;
	}

	@Override
	public Void deserialize(RepositoryNode node, NameReference contentProperty, InputStream inputStream) throws IOException {
		RepositoryContentData contentData = node.getProperty(contentProperty, RepositoryContentData.class);
		response.setHeader("Content-Length", Long.toString(contentData.getSize()));
		response.setContentType(contentData.getMimetype());
		
		String fileName = "export.bin";
		if (nameProperty != null) {
			fileName = (String) node.getProperty(nameProperty);
		}
		if (this.fileName != null) {
			fileName = this.fileName;
		}
		response.setHeader("Content-Disposition", (inline ? "inline" : "attachment") + "; filename=\"" + fileName + "\"");
		IOUtils.copy(inputStream, response.getOutputStream());
		
		return null;
	}

}