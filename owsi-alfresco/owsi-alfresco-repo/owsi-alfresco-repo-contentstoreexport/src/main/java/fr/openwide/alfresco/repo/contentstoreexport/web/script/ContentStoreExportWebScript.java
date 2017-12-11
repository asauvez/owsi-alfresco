package fr.openwide.alfresco.repo.contentstoreexport.web.script;

import java.io.IOException;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import fr.openwide.alfresco.repo.contentstoreexport.service.ContentStoreExportService;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptTransactionAllow;

@GenerateWebScript(
		url="/owsi/contentstoreexport.zip",
		shortName="Content store export",
		description="Export necessary files",
		transactionAllow=GenerateWebScriptTransactionAllow.READONLY,
		family="OWSI")
public class ContentStoreExportWebScript extends AbstractWebScript implements ApplicationContextAware {

	private ContentStoreExportService contentStoreExportService;
	
	@Override
	public void execute(WebScriptRequest req, WebScriptResponse resp) throws IOException {
		resp.setContentType("application/zip");
		contentStoreExportService.export(resp.getOutputStream(), 
				req.getParameter("paths"), 
				req.getParameter("queries"), 
				req.getParameter("nodeRefs"));
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		contentStoreExportService = applicationContext.getBean(ContentStoreExportService.class);
	}
}
