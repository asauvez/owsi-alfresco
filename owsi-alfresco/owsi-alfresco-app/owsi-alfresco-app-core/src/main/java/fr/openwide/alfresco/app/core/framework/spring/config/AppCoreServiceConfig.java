package fr.openwide.alfresco.app.core.framework.spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import fr.openwide.alfresco.app.core.authentication.service.AuthenticationService;
import fr.openwide.alfresco.app.core.authentication.service.impl.AuthenticationServiceImpl;
import fr.openwide.alfresco.app.core.authority.service.AuthorityService;
import fr.openwide.alfresco.app.core.authority.service.impl.AuthorityServiceImpl;
import fr.openwide.alfresco.app.core.node.service.NodeService;
import fr.openwide.alfresco.app.core.node.service.impl.NodeServiceImpl;
import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryRemoteBinding;
import fr.openwide.alfresco.app.core.search.service.NodeSearchService;
import fr.openwide.alfresco.app.core.search.service.impl.NodeSearchServiceImpl;

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
	public AuthorityService authorityService(NodeService nodeService) {
		return new AuthorityServiceImpl(nodeService);
	}

	@Bean
	public NodeSearchService nodeSearchService(NodeService nodeService) {
		return new NodeSearchServiceImpl(nodeService);
	}

}
