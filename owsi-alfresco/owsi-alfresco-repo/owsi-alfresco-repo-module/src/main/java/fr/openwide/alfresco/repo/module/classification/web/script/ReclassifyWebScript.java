package fr.openwide.alfresco.repo.module.classification.web.script;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.repo.dictionary.NamespaceDAO;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import fr.openwide.alfresco.repo.core.swagger.web.script.OwsiSwaggerWebScript;
import fr.openwide.alfresco.repo.module.classification.model.ReclassifyParams;
import fr.openwide.alfresco.repo.module.classification.service.ClassificationService;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptAuthentication;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptFormatDefault;
import fr.openwide.alfresco.repo.wsgenerator.annotation.SwaggerParameter;

@GenerateWebScript(
		url="/owsi/classification/reclassify",
		shortName="Reclasse les documents dans le plan de classement.",
		formatDefaultEnum=GenerateWebScriptFormatDefault.HTML,
		family=OwsiSwaggerWebScript.WS_FAMILY,
		authentication=GenerateWebScriptAuthentication.ADMIN,
		useViewFile=true,
		swaggerParameters={
			@SwaggerParameter(name="container", description = "Le type dont il faut reclassifier les nodes.", required=false),
			@SwaggerParameter(name="batchSize", description = "Le nombre de nodes à traiter par transaction.", required=false),
			@SwaggerParameter(name="olderThanMinutes", description = "Ne traite que les documents classifiés depuis plus que ce nombre de minutes.", required=false),
			@SwaggerParameter(name="withoutClassificationDate", description = "Ne traite que les documents classifiés qui n'ont pas de owsi:classificationDate.", required=false),
			@SwaggerParameter(name="customQuery", description = "Query FTS plus restrictive.", required=false),
			@SwaggerParameter(name="useCmis", description = "Utilise CMIS au lieu de FTS si true", required=false),
		})
public class ReclassifyWebScript extends DeclarativeWebScript {
	
	@Autowired
	private ClassificationService classificationService;
	@Autowired
	private NamespaceDAO namespaceDAO;
	
	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		ReclassifyParams params = new ReclassifyParams();

		String modelNameS = req.getParameter("container");
		if (StringUtils.isNotEmpty(modelNameS)) {
			QName modelName = QName.createQName(modelNameS, namespaceDAO);
			params.container(modelName);
		}

		String batchSizeS = req.getParameter("batchSize");
		if (StringUtils.isNotEmpty(batchSizeS)) {
			params.batchSize(Integer.parseInt(batchSizeS));
		}
		
		String olderThanMinutes = req.getParameter("olderThanMinutes");
		if (StringUtils.isNotEmpty(olderThanMinutes)) {
			params.olderThan(-1 * Integer.parseInt(olderThanMinutes), ChronoUnit.MINUTES);
		}
		
		String withoutClassificationDate = req.getParameter("withoutClassificationDate");
		if (StringUtils.isNotEmpty(withoutClassificationDate)) {
			params.withoutClassificationDate();
		}

		String customQuery = req.getParameter("customQuery");
		if (StringUtils.isNotEmpty(customQuery)) {
			params.getRestrictions()
				.custom(customQuery).of();
		}

		String useCmis = req.getParameter("useCmis");
		if (useCmis != null && Boolean.parseBoolean(useCmis)) {
			params.useCmis();
		}

		int total = classificationService.reclassify(params);
		
		Map<String, Object> model = new HashMap<>();
		model.put("total", total);
		return model;
	}

}

