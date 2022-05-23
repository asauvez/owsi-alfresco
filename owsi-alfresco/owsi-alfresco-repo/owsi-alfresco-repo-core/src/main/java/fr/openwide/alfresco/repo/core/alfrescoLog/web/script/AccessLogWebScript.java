package fr.openwide.alfresco.repo.core.alfrescoLog.web.script;

import java.text.SimpleDateFormat;
import java.util.Date;

import fr.openwide.alfresco.repo.core.swagger.web.script.OwsiSwaggerWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptAuthentication;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptFormatDefault;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptTransaction;
import fr.openwide.alfresco.repo.wsgenerator.annotation.SwaggerParameter;

@GenerateWebScript(
	url="/owsi/access.log",
	shortName="Retourne les 50 dernières lignes du fichier localhost_access_log.log.",
	authentication=GenerateWebScriptAuthentication.ADMIN,
	transaction=GenerateWebScriptTransaction.NONE,
	family=OwsiSwaggerWebScript.WS_FAMILY,
	formatDefaultEnum=GenerateWebScriptFormatDefault.TEXT,
	swaggerParameters = {
		@SwaggerParameter(name="lines", description="Nombre de lignes à retourner à la fin du fichier. -1 pour tout."),
		@SwaggerParameter(name="grep", description="Filtre le contenu"),
		@SwaggerParameter(name="grepv", description="Filtre négatifement le contenu")
	})
public class AccessLogWebScript extends LogDisplayWebScript {

	@Override
	protected String getLogFile() {
		//return "logs/localhost_access_log.2022-04-21.txt";
		return "logs/localhost_access_log." 
			+ new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".txt";
	}
}
