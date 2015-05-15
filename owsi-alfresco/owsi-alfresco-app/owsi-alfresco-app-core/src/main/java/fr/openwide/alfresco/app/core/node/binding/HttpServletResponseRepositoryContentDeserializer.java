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
	private final String name;
	private final NameReference nameProperty;
	
	public HttpServletResponseRepositoryContentDeserializer(HttpServletResponse response) {
		this(response, null, null);
	}
	public HttpServletResponseRepositoryContentDeserializer(HttpServletResponse response, String name) {
		this(response, name, null);
	}
	public HttpServletResponseRepositoryContentDeserializer(HttpServletResponse response, NameReference nameProperty) {
		this(response, null, nameProperty);
	}
	protected HttpServletResponseRepositoryContentDeserializer(HttpServletResponse response, String fileName, NameReference nameProperty) {
		this.response = response;
		this.name = fileName;
		this.nameProperty = nameProperty;
	}

	@Override
	public Void deserialize(RepositoryNode node, NameReference contentProperty, InputStream inputStream) throws IOException {
		RepositoryContentData contentData = node.getProperty(contentProperty, RepositoryContentData.class);
		response.setHeader("Content-Length", Long.toString(contentData.getSize()));
		response.setContentType(contentData.getMimetype());
		
		if (name != null) {
			response.setHeader("Content-Disposition", "attachment; filename=\"" + name + "\"");
		} else if (nameProperty != null) {
			response.setHeader("Content-Disposition", "attachment; filename=\"" + node.getProperty(nameProperty) + "\"");
		} else {
			response.setHeader("Content-Disposition", "inline; filename=\"" + node.getProperty(nameProperty) + "\"");
		}
		
		IOUtils.copy(inputStream, response.getOutputStream());
		
		return null;
	}

}