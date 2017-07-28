package fr.openwide.alfresco.app.core.framework.spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import fr.openwide.alfresco.api.core.authority.service.AuthorityRemoteService;
import fr.openwide.alfresco.api.core.node.service.NodeRemoteService;
import fr.openwide.alfresco.api.core.search.service.NodeSearchRemoteService;
import fr.openwide.alfresco.app.core.authentication.service.AuthenticationService;
import fr.openwide.alfresco.app.core.authentication.service.impl.AuthenticationServiceImpl;
import fr.openwide.alfresco.app.core.authority.service.AuthorityService;
import fr.openwide.alfresco.app.core.authority.service.impl.AuthorityServiceImpl;
import fr.openwide.alfresco.app.core.licence.service.LicenseService;
import fr.openwide.alfresco.app.core.licence.service.impl.LicenseServiceImpl;
import fr.openwide.alfresco.app.core.node.service.NodeService;
import fr.openwide.alfresco.app.core.node.service.impl.NodeServiceImpl;
import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryRemoteBinding;
import fr.openwide.alfresco.app.core.search.service.NodeSearchService;
import fr.openwide.alfresco.app.core.search.service.impl.NodeSearchServiceImpl;
import fr.openwide.alfresco.app.core.security.service.TicketReferenceProvider;
import fr.openwide.alfresco.app.core.site.service.SiteService;
import fr.openwide.alfresco.app.core.site.service.impl.SiteServiceImpl;
import fr.openwide.alfresco.component.model.authority.service.AuthorityModelService;
import fr.openwide.alfresco.component.model.authority.service.impl.AuthorityModelServiceImpl;
import fr.openwide.alfresco.component.model.node.service.NodeModelService;
import fr.openwide.alfresco.component.model.node.service.impl.NodeModelServiceImpl;
import fr.openwide.alfresco.component.model.search.service.NodeSearchModelService;
import fr.openwide.alfresco.component.model.search.service.impl.NodeSearchModelServiceImpl;

@Configuration
@Import(AppCoreRemoteBindingConfig.class)
public class AppCoreServiceConfig {

	@Autowired
	private AppCoreRemoteBindingConfig appCoreRemoteBindingConfig;
	
	@Autowired
	private Environment environment;

	@Bean
	public AuthenticationService authenticationService() {
		String authenticationHeader = environment.getRequiredProperty("application.authentication.repository.header.name");
		return new AuthenticationServiceImpl(
				appCoreRemoteBindingConfig.unauthenticatedRepositoryRemoteBinding(), 
				appCoreRemoteBindingConfig.requiringExplicitTicketRemoteBinding(),
				appCoreRemoteBindingConfig.authenticationRemoteBinding(), 
				authenticationHeader);
	}

	@Bean
	public NodeService nodeService(RepositoryRemoteBinding repositoryRemoteBinding) {
		return new NodeServiceImpl(repositoryRemoteBinding);
	}
	@Bean
	public NodeModelService nodeModelService(NodeRemoteService nodeService) {
		return new NodeModelServiceImpl(nodeService);
	}

	@Bean
	public AuthorityService authorityService(NodeService nodeService) {
		return new AuthorityServiceImpl(nodeService);
	}
	@Bean
	public AuthorityModelService authorityModelService(AuthorityRemoteService authorityService) {
		return new AuthorityModelServiceImpl(authorityService);
	}

	@Bean
	public NodeSearchService nodeSearchService(NodeService nodeService) {
		return new NodeSearchServiceImpl(nodeService);
	}
	@Bean
	public NodeSearchModelService nodeSearchModelService(NodeSearchRemoteService nodeSearchService) {
		return new NodeSearchModelServiceImpl(nodeSearchService);
	}

	@Bean
	public LicenseService licenceService(TicketReferenceProvider ticketProvider) {
		return new LicenseServiceImpl(appCoreRemoteBindingConfig.userAwareRepositoryRemoteBinding(ticketProvider));
	}
	
	@Bean
	public SiteService siteService(AuthorityService authorityService, NodeSearchService nodeSearchService, 
			TicketReferenceProvider ticketProvider) {
		return new SiteServiceImpl(authorityService, nodeSearchService, 
				appCoreRemoteBindingConfig.shareRemoteBinding(ticketProvider));
	}

}
