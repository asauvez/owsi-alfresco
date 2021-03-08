package fr.openwide.alfresco.repo.solraudit.service.impl;

import java.io.CharArrayWriter;
import java.io.PrintWriter;

import org.springframework.beans.factory.annotation.Autowired;

import fr.openwide.alfresco.repo.solraudit.service.SolrAuditService;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateCron;

@GenerateCron(
	id = "owsi.solraudit.cron",
	cronExpression = "${owsi.solraudit.cronExpression:0 0 5 ? * SUN}",
	enable = "${owsi.solraudit.enabled:true}",
	logAsInfo = true
)
public class SolrAuditCron implements Runnable {

	@Autowired
	private SolrAuditService solrAuditService;
	
	@Override
	public void run() {
		// TODO: Poser dans un fichier Excel dans un site

		CharArrayWriter out = new CharArrayWriter();
		solrAuditService.generateAudit(new PrintWriter(out));
	}
}
