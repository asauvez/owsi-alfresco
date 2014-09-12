package fr.openwide.alfresco.app.core.node.serializer;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import fr.openwide.alfresco.repository.api.node.model.RepositoryContentData;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.node.serializer.RepositoryContentDeserializer;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class HttpServletResponseRepositoryContentDeserializer implements RepositoryContentDeserializer<Void> {

	private final HttpServletResponse response;
	private String fileName;
	private NameReference fileNameProperty;
	
	public HttpServletResponseRepositoryContentDeserializer(HttpServletResponse response) {
		this.response = response;
	}
	public HttpServletResponseRepositoryContentDeserializer(HttpServletResponse response, String fileName) {
		this(response);
		this.fileName = fileName;
	}
	public HttpServletResponseRepositoryContentDeserializer(HttpServletResponse response, NameReference fileNameProperty) {
		this(response);
		this.fileNameProperty = fileNameProperty;
	}

	@Override
	public Void deserialize(RepositoryNode node, NameReference contentProperty, InputStream inputStream) throws IOException {
		RepositoryContentData contentData = (RepositoryContentData) node.getProperties().get(contentProperty);
		response.setContentLength(contentData.getSize().intValue());
		response.setContentType(contentData.getMimetype());
		
		if (fileName != null) {
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		} else if (fileNameProperty != null) {
			response.setHeader("Content-Disposition", "attachment; filename=\"" + node.getProperties().get(fileNameProperty) + "\"");
		} else {
			response.setHeader("Content-Disposition", "inline; filename=\"" + node.getProperties().get(fileNameProperty) + "\"");
		}
		
		IOUtils.copy(inputStream, response.getOutputStream());
		
		return null;
	}

}