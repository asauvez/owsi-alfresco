package fr.openwide.alfresco.repo.solraudit.webscript;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import fr.openwide.alfresco.repo.solraudit.service.SolrAuditService;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptAuthentication;

/*
 * http://localhost:8080/alfresco/s/owsi/solraudit.csv
 */
@GenerateWebScript(
	url="/owsi/solraudit.csv",
	description="Calcul des statistiques dans Solr",
	authentication=GenerateWebScriptAuthentication.ADMIN,
	family="OWSI")
public class SolrAuditWebScript extends AbstractWebScript {

	@Autowired
	private SolrAuditService solrAuditService;
	
	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		res.setContentType("text/csv");
		
		solrAuditService.generateAudit(
				new PrintWriter(res.getWriter()));
	}
}
