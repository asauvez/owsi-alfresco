package fr.openwide.alfresco.repo.contentstoreexport.web.script;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import fr.openwide.alfresco.repo.contentstoreexport.service.ContentStoreExportService;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptAuthentication;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptTransactionAllow;
import fr.openwide.alfresco.repo.wsgenerator.annotation.SwaggerParameter;

@GenerateWebScript(
		url={"/owsi/contentstoreexport", "/owsi/contentstoreexport.zip"},
		shortName="Content store export",
		description="Export necessary files",
		transactionAllow=GenerateWebScriptTransactionAllow.READONLY,
		authentication=GenerateWebScriptAuthentication.ADMIN,
		family="OWSI",
		swaggerParameters={
			@SwaggerParameter(name="paths", description="Liste de chemins à exporter séparés par des virgules (défaut vide)"),
			@SwaggerParameter(name="queries", description="Liste de queries à exporter séparées par des virgules (défaut vide)"),
			@SwaggerParameter(name="nodeRefs", description="Liste de nodeRef racines à exporter séparés par des virgules (défaut vide)"),
			@SwaggerParameter(name="exportContent", description="S'il faut exporter ou non le contenu (défaut true)"),
		})
public class ContentStoreExportWebScript extends AbstractWebScript implements ApplicationContextAware {

	private ContentStoreExportService contentStoreExportService;
	
	@Override
	public void execute(WebScriptRequest req, WebScriptResponse resp) throws IOException {
		resp.setContentType("application/zip");
		resp.setHeader("Content-Disposition", "attachment; filename=\"contentstoreexport-" 
				+ new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + ".zip\"");
		String exportContent = req.getParameter("exportContent");
		contentStoreExportService.export(resp.getOutputStream(), 
				req.getParameter("paths"), 
				req.getParameter("queries"), 
				req.getParameter("nodeRefs"),
				(exportContent != null) ? Boolean.parseBoolean(exportContent) : true);
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		contentStoreExportService = applicationContext.getBean(ContentStoreExportService.class);
	}
}
