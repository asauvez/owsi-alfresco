package fr.openwide.alfresco.repo.contentstoreexport.web.script;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
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
			@SwaggerParameter(name="paths", description="Liste de chemins à exporter (défaut vide)"),
			@SwaggerParameter(name="queries", description="Liste de queries à exporter (défaut vide)"),
			@SwaggerParameter(name="nodeRefs", description="Liste de nodeRef racines à exporter (défaut vide)"),
			@SwaggerParameter(name="sites", description="Liste de sites à exporter (défaut vide)"),
			@SwaggerParameter(name="exportBase", description="Export tout les noeuds minimums nécessaires à lancer Alfresco (défaut true)"),
			@SwaggerParameter(name="exportAll", description="Export tout les noeuds (défaut false)"),
			@SwaggerParameter(name="exportContent", description="S'il faut exporter ou non le contenu (défaut true)"),
			@SwaggerParameter(name="exportVersions", description="S'il faut exporter ou non les anciennes versions (défaut true)"),
			@SwaggerParameter(name="writeTo", description="Emplacement où écrire sur disque le Zip (défaut renvoie juste le Zip)"),
			@SwaggerParameter(name="pathType", description="Type d'export contentstore, alfresco ou bulk (défaut contentstore)"),
			@SwaggerParameter(name="since", description="N'exporte que les données modifiées depuis cette période (ex: P1D)"),
		})
public class ContentStoreExportWebScript extends AbstractWebScript implements ApplicationContextAware {

	private ContentStoreExportService contentStoreExportService;
	
	@Override
	public void execute(WebScriptRequest req, WebScriptResponse resp) throws IOException {
		ContentStoreExportParams params = new ContentStoreExportParams();
		for (String paramName : req.getParameterNames()) {
			String[] paramValues = req.getParameterValues(paramName);
			try {
				Field field = ContentStoreExportParams.class.getField(paramName);
				Class<?> type = field.getType();
				if (! type.isArray() && paramValues.length != 1) {
					throw new IllegalStateException("You can not specify the param " + paramName + " more than once.");
				}
				Object convertValue = convert(paramValues, type);
				field.set(params, convertValue);
			} catch (Exception e) {
				throw new IllegalArgumentException(paramName, e);
			}
		}
		
		resp.setContentType("application/zip");
		resp.setHeader("Content-Disposition", "attachment; filename=\"contentstoreexport-" 
				+ new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) 
				+ ".zip\"");
		
		OutputStream outputStream = resp.getOutputStream();
		try {
			if (params.writeTo != null) {
				 outputStream = new TeeOutputStream(outputStream, new FileOutputStream(params.writeTo));
			}
			
			contentStoreExportService.export(outputStream, params);
			outputStream.flush();
		} finally {
			outputStream.close();
		}
	}
	
	private Object convert(String[] paramValues, Class<?> type) {
		if (type == String[].class) {
			return paramValues;
		} else if (type == String.class){
			return paramValues[0];
		} else if (type == boolean.class || type == Boolean.class){
			return Boolean.valueOf(paramValues[0]);
		} else if (type == int.class || type == Integer.class){
			return Integer.valueOf(paramValues[0]);
		} else {
			throw new IllegalStateException(type.toString());
		}
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		contentStoreExportService = applicationContext.getBean(ContentStoreExportService.class);
	}
}
