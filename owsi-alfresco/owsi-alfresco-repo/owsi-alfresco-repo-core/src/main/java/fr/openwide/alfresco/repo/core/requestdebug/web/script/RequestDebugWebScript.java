package fr.openwide.alfresco.repo.core.requestdebug.web.script;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import fr.openwide.alfresco.repo.core.swagger.web.script.OwsiSwaggerWebScript;
import fr.openwide.alfresco.repo.core.swagger.web.script.SwaggerWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptAuthentication;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptFormatDefault;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptTransaction;

@GenerateWebScript(
		url="/owsi/request-debug",
		description="Retourne des informations sur la requête",
		authentication=GenerateWebScriptAuthentication.ADMIN,
		transaction=GenerateWebScriptTransaction.NONE,
		family=OwsiSwaggerWebScript.WS_FAMILY,
		formatDefaultEnum=GenerateWebScriptFormatDefault.TEXT)
public class RequestDebugWebScript extends SwaggerWebScript {

	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		PrintWriter out = new PrintWriter(res.getWriter());
		
		out.println("URL: " + req.getURL());
		
		out.println("Headers:");
		for (String headerName : req.getHeaderNames()) {
			String[] headerValues = req.getHeaderValues(headerName);
			out.append("  ").append(headerName).append(": ").append(Arrays.asList(headerValues).toString()).append("\n");
		}
		
		out.println("Params:");
		for (String paramName : req.getParameterNames()) {
			String[] paramValues = req.getParameterValues(paramName);
			out.append("  ").append(paramName).append("=").append(Arrays.asList(paramValues).toString()).append("\n");
		}
		
		out.flush();
		out.close();
	}
}
