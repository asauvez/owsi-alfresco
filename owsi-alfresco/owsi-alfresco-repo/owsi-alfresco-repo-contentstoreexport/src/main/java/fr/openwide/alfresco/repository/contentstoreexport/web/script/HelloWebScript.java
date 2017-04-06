package fr.openwide.alfresco.repository.contentstoreexport.web.script;

import java.io.IOException;

import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import fr.openwide.alfresco.repository.contentstoreexport.service.ContentStoreExportService;

public class HelloWebScript extends AbstractWebScript {

	private ContentStoreExportService contentStoreExportService;
	
	@Override
	public void execute(WebScriptRequest req, WebScriptResponse resp) throws IOException {
		resp.setContentType("application/zip");
		contentStoreExportService.export(resp.getOutputStream());
	}
	
	public void setContentStoreExportService(ContentStoreExportService contentStoreExportService) {
		this.contentStoreExportService = contentStoreExportService;
	}
}
