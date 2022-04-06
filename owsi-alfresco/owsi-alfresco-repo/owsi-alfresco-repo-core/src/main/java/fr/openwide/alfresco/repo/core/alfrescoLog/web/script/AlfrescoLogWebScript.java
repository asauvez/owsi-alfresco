package fr.openwide.alfresco.repo.core.alfrescoLog.web.script;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Deque;
import java.util.LinkedList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import fr.openwide.alfresco.repo.core.swagger.web.script.OwsiSwaggerWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptAuthentication;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptFormatDefault;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptTransaction;
import fr.openwide.alfresco.repo.wsgenerator.annotation.SwaggerParameter;

@GenerateWebScript(
	url="/owsi/alfresco.log",
	shortName="Retourne les 50 dernières lignes du fichier alfresco.log.",
	authentication=GenerateWebScriptAuthentication.ADMIN,
	transaction=GenerateWebScriptTransaction.NONE,
	family=OwsiSwaggerWebScript.WS_FAMILY,
	formatDefaultEnum=GenerateWebScriptFormatDefault.TEXT,
	swaggerParameters = {
		@SwaggerParameter(name="lines", description="Nombre de lignes à retourner à la fin du fichier. -1 pour tout.")
	})
public class AlfrescoLogWebScript extends AbstractWebScript {

	protected String getLogFile() {
		return "logs/alfresco.log";
	}
	
	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		String linesS = req.getParameter("lines");
		int nbLines = (linesS != null) ? Integer.parseInt(linesS) : 50;

		res.setContentType("text/plain");
		
		File file = new File(getLogFile());
		if (nbLines == -1) {
			FileUtils.copyFile(file, res.getOutputStream());
		} else {
			Deque<String> lines = new LinkedList<>();
			try (ReversedLinesFileReader reader = new ReversedLinesFileReader(file)) {
				for (int i=0; i<nbLines; i++) {
					String line = reader.readLine();
					if (line == null) {
						break;
					}
					lines.addFirst(line);
				}
			}
			try (PrintWriter out = new PrintWriter(res.getWriter())) {
				for (String line : lines) {
					out.println(line);
				}
			}
		}
	}
}
