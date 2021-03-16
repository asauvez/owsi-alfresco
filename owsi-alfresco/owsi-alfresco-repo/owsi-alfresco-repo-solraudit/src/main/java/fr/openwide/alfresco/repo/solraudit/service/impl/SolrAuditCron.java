package fr.openwide.alfresco.repo.solraudit.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import fr.openwide.alfresco.repo.solraudit.service.SolrAuditService;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateCron;

@GenerateCron(
	id = "owsi.solraudit.cron",
	//cronExpression = "${owsi.solraudit.cronExpression:0 0 5 ? * SUN}",
	cronExpression = "${owsi.solraudit.cronExpression}",
	enable = "${owsi.solraudit.enabled:true}",
	logAsInfo = true
)
public class SolrAuditCron implements Runnable {

	@Autowired
	private SolrAuditService solrAuditService;
	
	@Override
	public void run() {
		solrAuditService.storeAudit();
	}
}
