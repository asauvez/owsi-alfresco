package fr.openwide.alfresco.demo;

import java.io.IOException;

import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptFormatDefault;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptTransactionAllow;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.WebScriptMethod;
import fr.openwide.alfresco.repo.wsgenerator.annotation.SwaggerParameter;
import fr.openwide.alfresco.repo.wsgenerator.annotation.SwaggerResponse;

/**
 * http://localhost:8080/alfresco/s/demo/hello
 */
@GenerateWebScript(
	method = WebScriptMethod.GET,
	url = "/demo/hello",
	shortName = "Retourne hello world",
	transactionAllow = GenerateWebScriptTransactionAllow.READONLY,
	formatDefaultEnum = GenerateWebScriptFormatDefault.TEXT,
	family=DemoSwaggerWebScript.WS_FAMILY,
	swaggerParameters = {
		@SwaggerParameter(name="name", description="Nom de la personne"),
	},
	swaggerResponses = {
		@SwaggerResponse(statusCode = 200, description = "Message de bienvenu")
	}
)
public class DemoHelloWS extends AbstractWebScript {
	
	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		String name = req.getParameter("name");
		res.getWriter().append("Hello " + ((name != null) ? name : "World"));
	}
}
