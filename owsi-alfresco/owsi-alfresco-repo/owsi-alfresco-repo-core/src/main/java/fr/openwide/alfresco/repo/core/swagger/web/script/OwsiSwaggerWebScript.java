package fr.openwide.alfresco.repo.core.swagger.web.script;

import org.springframework.extensions.webscripts.WebScript;

import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptAuthentication;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptTransaction;

@GenerateWebScript(
		url="/owsi/swagger.yaml",
		description="Retourne un Swagger pour les WS OWSI",
		authentication=GenerateWebScriptAuthentication.NONE,
		transaction=GenerateWebScriptTransaction.NONE,
		family=OwsiSwaggerWebScript.WS_FAMILY)
public class OwsiSwaggerWebScript extends SwaggerWebScript {

	public static final String WS_FAMILY = "OWSI";
	
	@Override
	public boolean isWebScriptFiltered(GenerateWebScript annotation, WebScript webscript) {
		return super.isWebScriptFiltered(annotation, webscript) 
			&& WS_FAMILY.equals(annotation.family());
	}
}
