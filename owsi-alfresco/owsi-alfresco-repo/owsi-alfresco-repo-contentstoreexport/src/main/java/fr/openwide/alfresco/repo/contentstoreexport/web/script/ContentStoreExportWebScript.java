package fr.openwide.alfresco.repo.contentstoreexport.web.script;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.output.TeeOutputStream;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import fr.openwide.alfresco.repo.contentstoreexport.model.ContentStoreExportParams;
import fr.openwide.alfresco.repo.contentstoreexport.service.ContentStoreExportService;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptAuthentication;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptTransactionAllow;
import fr.openwide.alfresco.repo.wsgenerator.annotation.SwaggerParameter;

/**
 * http://localhost:8080/alfresco/s/owsi/contentstoreexport.zip
 */
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
			@SwaggerParameter(name="writeTo", description="Emplacement où écrire sur disque le Zip (défaut renvoie juste le Zip)"),
		})
public class ContentStoreExportWebScript extends AbstractWebScript implements ApplicationContextAware {

	private ContentStoreExportService contentStoreExportService;
	
	@Override
	public void execute(WebScriptRequest req, WebScriptResponse resp) throws IOException {
		ContentStoreExportParams params = new ContentStoreExportParams();
		for (String paramName : req.getParameterNames()) {
			String paramValue = req.getParameter(paramName);
			try {
				ContentStoreExportParams.class.getField(paramName).set(params, paramValue);
			} catch (Exception e) {
				throw new IllegalArgumentException(paramName, e);
			}
		}
		
		resp.setContentType("application/zip");
		resp.setHeader("Content-Disposition", "attachment; filename=\"contentstoreexport-" 
				+ new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + ".zip\"");
		
		OutputStream outputStream = resp.getOutputStream();
		if (req.getParameter("writeTo") != null) {
			 outputStream = new TeeOutputStream(outputStream, new FileOutputStream(req.getParameter("writeTo")));
		}
		
		contentStoreExportService.export(outputStream, params);
		
		outputStream.flush();
		outputStream.close();
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		contentStoreExportService = applicationContext.getBean(ContentStoreExportService.class);
	}
}
