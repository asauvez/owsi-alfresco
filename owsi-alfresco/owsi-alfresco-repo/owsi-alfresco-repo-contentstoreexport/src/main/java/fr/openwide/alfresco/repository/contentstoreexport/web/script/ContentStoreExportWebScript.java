package fr.openwide.alfresco.repository.contentstoreexport.web.script;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import fr.openwide.alfresco.api.core.remote.model.endpoint.RemoteEndpoint.RemoteEndpointMethod;
import fr.openwide.alfresco.repository.contentstoreexport.service.ContentStoreExportService;
import fr.openwide.alfresco.repository.wsgenerator.annotation.GenerateWebScript;
import fr.openwide.alfresco.repository.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptTransactionAllow;

@GenerateWebScript(
		url="/owsi/contentstoreexport.zip",
		method=RemoteEndpointMethod.GET,
		shortName="Content store export",
		description="Export necessary files",
		transactionAllow=GenerateWebScriptTransactionAllow.READONLY,
		formatDefault="zip")
public class ContentStoreExportWebScript extends AbstractWebScript {

	@Autowired
	private ContentStoreExportService contentStoreExportService;
	
	@Override
	public void execute(WebScriptRequest req, WebScriptResponse resp) throws IOException {
		resp.setContentType("application/zip");
		contentStoreExportService.export(resp.getOutputStream(), 
				req.getParameter("paths"), 
				req.getParameter("queries"), 
				req.getParameter("nodeRefs"));
	}
	
	public void setContentStoreExportService(ContentStoreExportService contentStoreExportService) {
		this.contentStoreExportService = contentStoreExportService;
	}
}
