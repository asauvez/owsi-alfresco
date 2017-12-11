package fr.openwide.alfresco.repo.module.classification.web.script;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.repo.module.classification.service.ClassificationService;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript;

/**
 * http://localhost:8080/alfresco/service/owsi/admin/setModuleCurrentVersion?module={moduleId}&version={version}
 * http://localhost:8080/alfresco/service/owsi/admin/setModuleCurrentVersion?module=owsi-alfresco-repo-contentstoreexport&version=0.4.0
 */
@GenerateWebScript(
		url={
			"/owsi/classification/reclassify",
			"/owsi/classification/reclassify?model=demo:document&batchSize=100"
		},
		description="Reclasse les documents dans le plan de classement.",
		formatDefault="html",
		family="OWSI",
		useViewFile=true)
public class ReclassifyWebScript extends DeclarativeWebScript {
	
	@Autowired
	private ClassificationService classificationService;
	
	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		String batchSizeS = req.getParameter("batchSize");
		int batchSize = (batchSizeS != null) ? Integer.parseInt(batchSizeS) : ClassificationService.DEFAULT_RECLASSIFY_BATCH_SIZE;

		String modelNameS = req.getParameter("model");
		int total;
		if (modelNameS != null) {
			NameReference modelName = NameReference.create(modelNameS);
			total = classificationService.reclassify(modelName, batchSize);
		} else {
			total = classificationService.reclassifyAll(batchSize);
		}
		Map<String, Object> model = new HashMap<>();
		model.put("total", total);
		return model;
	}

}

