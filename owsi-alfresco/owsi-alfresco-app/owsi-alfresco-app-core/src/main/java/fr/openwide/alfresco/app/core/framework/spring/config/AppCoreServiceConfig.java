package fr.openwide.alfresco.app.core.framework.spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.openwide.alfresco.api.core.util.ThresholdBufferFactory;
import fr.openwide.alfresco.app.core.authority.service.AuthorityService;
import fr.openwide.alfresco.app.core.authority.service.impl.AuthorityServiceImpl;
import fr.openwide.alfresco.app.core.licence.service.LicenseService;
import fr.openwide.alfresco.app.core.licence.service.impl.LicenseServiceImpl;
import fr.openwide.alfresco.app.core.node.service.NodeService;
import fr.openwide.alfresco.app.core.node.service.impl.NodeServiceImpl;
import fr.openwide.alfresco.app.core.search.service.NodeSearchService;
import fr.openwide.alfresco.app.core.search.service.impl.NodeSearchServiceImpl;
import fr.openwide.alfresco.app.core.site.service.SiteService;
import fr.openwide.alfresco.app.core.site.service.impl.SiteServiceImpl;
import fr.openwide.alfresco.component.model.authority.service.AuthorityModelService;
import fr.openwide.alfresco.component.model.authority.service.impl.AuthorityModelServiceImpl;
import fr.openwide.alfresco.component.model.node.service.NodeModelService;
import fr.openwide.alfresco.component.model.node.service.impl.NodeModelServiceImpl;
import fr.openwide.alfresco.component.model.search.service.NodeSearchModelService;
import fr.openwide.alfresco.component.model.search.service.impl.NodeSearchModelServiceImpl;

@Configuration
public class AppCoreServiceConfig {

	@Autowired
	private AppCoreSecurityConfig appCoreSecurityConfig;

	@Bean
	public NodeService nodeService() {
		return new NodeServiceImpl(appCoreSecurityConfig.userAwareRepositoryRemoteBinding());
	}
	@Bean
	public NodeModelService nodeModelService() {
		return new NodeModelServiceImpl(nodeService());
	}

	@Bean
	public AuthorityService authorityService() {
		return new AuthorityServiceImpl(appCoreSecurityConfig.userAwareRepositoryRemoteBinding());
	}
	@Bean
	public AuthorityModelService authorityModelService() {
		return new AuthorityModelServiceImpl(authorityService());
	}

	@Bean
	public NodeSearchService nodeSearchService() {
		return new NodeSearchServiceImpl(appCoreSecurityConfig.userAwareRepositoryRemoteBinding());
	}
	@Bean
	public NodeSearchModelService nodeSearchModelService() {
		return new NodeSearchModelServiceImpl(nodeSearchService());
	}

	@Bean
	public LicenseService licenceService() {
		return new LicenseServiceImpl(appCoreSecurityConfig.userAwareRepositoryRemoteBinding());
	}
	
	@Bean
	public SiteService siteService() {
		return new SiteServiceImpl(authorityService(), nodeSearchService(), 
				appCoreSecurityConfig.shareRemoteBinding());
	}

	@Bean
	public ThresholdBufferFactory thresholdBufferFactory(
			@Value("${owsi.thresholdTempFiles.memoryThreshold}") int memoryThreshold, 
			@Value("${owsi.thresholdTempFiles.maxContentSize}") long maxContentSize, 
			@Value("${owsi.thresholdTempFiles.encrypt}") boolean encrypt) {

		return ThresholdBufferFactory.newInstance(null, memoryThreshold, maxContentSize, encrypt);
	}
}
