package fr.openwide.alfresco.repo.solraudit.service.impl;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import fr.openwide.alfresco.repo.solraudit.service.SolrAuditService;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateCron;

@GenerateCron(
	id = "owsi.solraudit.cron",
	cronExpression = "${owsi.solraudit.cronExpression}",
	enable = "${owsi.solraudit.enabled:true}",
	logAsInfo = true
)
public class SolrAuditCron implements Runnable {

	@Autowired
	private SolrAuditService solrAuditService;
	@Autowired @Qualifier("global-properties")
	private Properties globalProperties;
	
	@Override
	public void run() {
		boolean includeTrashCan = "true".equals(globalProperties.getProperty("owsi.solraudit.includeTrashCan", "true"));
		solrAuditService.storeAudit(includeTrashCan);
	}
}
