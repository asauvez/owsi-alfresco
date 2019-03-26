package fr.openwide.alfresco.repo.module.classification.web.script;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import fr.openwide.alfresco.repo.module.classification.service.ClassificationService;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript;

/**
 * http://localhost:8080/alfresco/service/owsi/classification/clearcache
 */
@GenerateWebScript(
		url="/owsi/classification/clearcache",
		description="Vide les caches de classification.",
		formatDefault="html",
		family="OWSI",
		useViewFile=true)
public class ClearCacheWebScript extends DeclarativeWebScript {
	
	@Autowired
	private ClassificationService classificationService;
	
	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		classificationService.clearCaches();
		return new HashMap<>();
	}

}

