package fr.openwide.alfresco.repo.core.bootstrap.web.script;

import java.io.IOException;

import org.activiti.engine.impl.util.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import fr.openwide.alfresco.repo.core.bootstrap.service.impl.RunAtEveryLaunchService;
import fr.openwide.alfresco.repo.core.swagger.web.script.OwsiSwaggerWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptAuthentication;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptFormatDefault;

@GenerateWebScript(
	url="/owsi/admin/runPatches",
	shortName="Lance les patchs qui se lancent à chaque démarrage.",
	formatDefaultEnum=GenerateWebScriptFormatDefault.TEXT,
	family=OwsiSwaggerWebScript.WS_FAMILY,
	authentication=GenerateWebScriptAuthentication.ADMIN
)
public class RunPatchesWebScript extends AbstractWebScript {
	
	@Autowired private RunAtEveryLaunchService runAtEveryLaunchService;
	
	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		res.setContentType("application/json");

		runAtEveryLaunchService.launchPatches();
		
		res.getWriter().append(new JSONObject().put("ok", true).toString());
	}
}

