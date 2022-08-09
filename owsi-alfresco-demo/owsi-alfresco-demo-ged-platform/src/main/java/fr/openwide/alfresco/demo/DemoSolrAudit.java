package fr.openwide.alfresco.demo;

import org.alfresco.model.ContentModel;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import fr.openwide.alfresco.repo.solraudit.service.SolrAuditService;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateService;

@GenerateService
public class DemoSolrAudit implements InitializingBean {
	
	@Autowired private SolrAuditService solrAuditService;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		solrAuditService.registerDateGroupMonthCreated(ContentModel.TYPE_CONTENT, 
				DemoModel.auditInfo.createdMonth.getQName());
		solrAuditService.registerLogSize(ContentModel.TYPE_CONTENT, 
				DemoModel.auditInfo.sizeLog.getQName());
	}
}
