package fr.openwide.alfresco.demo;

import org.alfresco.model.ContentModel;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import fr.openwide.alfresco.repo.remote.conversion.service.ConversionService;
import fr.openwide.alfresco.repo.solraudit.service.SolrAuditService;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateService;

@GenerateService
public class DemoSolrAudit implements InitializingBean {
	
	@Autowired private SolrAuditService solrAuditService;
	@Autowired private ConversionService conversionService;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		solrAuditService.registerDateGroup(ContentModel.TYPE_CONTENT, 
				conversionService.getRequired(DemoModel.auditInfo.createdMonth.getNameReference()));
		solrAuditService.registerLogSize(ContentModel.TYPE_CONTENT, 
				conversionService.getRequired(DemoModel.auditInfo.sizeLog.getNameReference()));
	}
}
