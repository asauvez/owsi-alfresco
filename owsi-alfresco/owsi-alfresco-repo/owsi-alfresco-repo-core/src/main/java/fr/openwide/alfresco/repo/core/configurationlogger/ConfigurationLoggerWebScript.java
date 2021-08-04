package fr.openwide.alfresco.repo.core.configurationlogger;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import fr.openwide.alfresco.repo.core.swagger.web.script.OwsiSwaggerWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptAuthentication;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptFormatDefault;

/**
 * http://localhost:8080/alfresco/service/owsi/admin/configuration
 */
@GenerateWebScript(
		url="/owsi/admin/configuration",
		shortName="Renvoi la configuration tel qu'elle a été généré au démarrage dans les logs",
		formatDefaultEnum=GenerateWebScriptFormatDefault.TEXT,
		authentication=GenerateWebScriptAuthentication.ADMIN,
		family=OwsiSwaggerWebScript.WS_FAMILY)
public class ConfigurationLoggerWebScript extends AbstractWebScript {
	
	@Autowired
	@Qualifier("owsi.configurationlogger")
	private ConfigurationLogger configurationLogger;

	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		res.getWriter().append(configurationLogger.getMessagesGenerated());
	}
}

