package fr.openwide.alfresco.repo.dictionary.search.web.script;

import java.io.IOException;
import java.util.function.Consumer;

import org.activiti.engine.impl.util.json.JSONObject;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import fr.openwide.alfresco.component.model.search.model.restriction.RestrictionBuilder;
import fr.openwide.alfresco.repo.core.swagger.web.script.OwsiSwaggerWebScript;
import fr.openwide.alfresco.repo.dictionary.search.model.BatchSearchQueryBuilder;
import fr.openwide.alfresco.repo.dictionary.search.service.NodeSearchModelRepositoryService;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptAuthentication;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptFormatDefault;
import fr.openwide.alfresco.repo.wsgenerator.annotation.SwaggerParameter;

@GenerateWebScript(
	url="/owsi/batch/replacePropertiesValue?where={where}",
	shortName="Remplace en masse les valeurs de plusieurs propriétés sur toutes les nodes correspondantes à un critère.",
	description = "http://localhost:8080/alfresco/s/owsi/batch/replacePropertiesValue\n" + 
			"		?where==exif:manufacturer:value:\"OLYMPUS OPTICAL CO.,LTD\"\n" + 
			"		&exif:software=\"New software 2.0\"",
	formatDefaultEnum=GenerateWebScriptFormatDefault.TEXT,
	family=OwsiSwaggerWebScript.WS_FAMILY,
	authentication=GenerateWebScriptAuthentication.USER,
	swaggerParameters={
		@SwaggerParameter(name="where", description = "Le critère de recherche en FTS : =foo:bar:'value'", required=true),
		@SwaggerParameter(name="cm:title", description = "Exemple de propriété à mettre à jour", required=true),
		@SwaggerParameter(name="cm:description", description = "Autre exemple", required=true),
	})
public class ReplacePropertiesValueWebScript extends AbstractWebScript {
	
	@Autowired private NodeSearchModelRepositoryService nodeSearchModelRepositoryService;
	@Autowired private NodeService nodeService;
	@Autowired @Qualifier("NamespaceService") private NamespacePrefixResolver prefixResolver;
	
	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		res.setContentType("application/json");

		String where = req.getParameter("where");
		String[] parameters = req.getParameterNames();
		
		BatchSearchQueryBuilder builder = new BatchSearchQueryBuilder()
			.configurationName(
				"owsi.replacePropertiesValue", 
				"owsi.replacePropertiesValue." + where);
		
		// Condition
		builder.restriction(new RestrictionBuilder()
				.custom(where).of());
		
		// Action
		builder.consumer(new Consumer<NodeRef>() {
			@Override
			public void accept(NodeRef nodeRef) {
				for (String parameter : parameters) {
					if (parameter.contains(":")) {
						QName propertyQName = QName.resolveToQName(prefixResolver, parameter);
						String newValue = req.getParameter(parameter);
						nodeService.setProperty(nodeRef, propertyQName, newValue);
					}
				}
			}
		});
		int total = nodeSearchModelRepositoryService.searchBatch(builder);
		
		res.getWriter().append(new JSONObject().put("total", total).toString());
	}
}

