package fr.openwide.alfresco.demo;

import org.springframework.extensions.webscripts.WebScript;

import fr.openwide.alfresco.repo.core.swagger.web.script.OwsiSwaggerWebScript;
import fr.openwide.alfresco.repo.core.swagger.web.script.SwaggerWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptAuthentication;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptTransaction;

/**
 * http://localhost:8080/alfresco/s/demo/swagger.yaml
 */
@GenerateWebScript(
		url="/demo/swagger.yaml",
		description="Retourne un Swagger pour les WS Demo",
		authentication=GenerateWebScriptAuthentication.NONE,
		transaction=GenerateWebScriptTransaction.NONE,
		family=DemoSwaggerWebScript.WS_FAMILY)
public class DemoSwaggerWebScript extends SwaggerWebScript {

	public static final String WS_FAMILY = "DEMO";
	
	@Override
	public boolean isWebScriptFiltered(GenerateWebScript annotation, WebScript webscript) {
		return super.isWebScriptFiltered(annotation, webscript) 
			&& (   WS_FAMILY.equals(annotation.family()) 
			    || OwsiSwaggerWebScript.WS_FAMILY.equals(annotation.family()));
	}
}
