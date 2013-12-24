package fr.openwide.alfresco.app.core.framework.spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.SerializationFeature;

import fr.openwide.alfresco.app.core.AlfrescoAppCorePackage;
import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryPayloadParameterHandler;
import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryRemoteBinding;
import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryRemoteExceptionHandler;
import fr.openwide.alfresco.app.core.security.service.UserService;

@Configuration
@ComponentScan(
	basePackageClasses = {
		AlfrescoAppCorePackage.class
	},
	// https://jira.springsource.org/browse/SPR-8808
	// on veut charger de manière explicite le contexte ; de ce fait,
	// on ignore l'annotation @Configuration sur le scan de package.
	excludeFilters = @Filter(Configuration.class)
)
public class AppCoreConfig {

	@Autowired
	private Environment environment;

	@Bean
	public RestTemplate remoteRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new RepositoryRemoteExceptionHandler());
		((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setBufferRequestBody(false);
		
		for (HttpMessageConverter<?> converter : restTemplate.getMessageConverters()) {
			if (converter instanceof MappingJackson2HttpMessageConverter) {
				((MappingJackson2HttpMessageConverter) converter).getObjectMapper().configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false);
				break;
			}
		}
		
		return restTemplate;
	}

	@Bean
	public RepositoryPayloadParameterHandler RepositoryPayloadParameterHandler() {
		return new RepositoryPayloadParameterHandler();
	}

	@Bean
	@Primary
	public RepositoryRemoteBinding userAwareRepositoryRemoteBinding(RestTemplate restTemplate, UserService userService) {
		String repositoryUri = environment.getRequiredProperty("application.repository.root.uri");
		String ticketName = environment.getRequiredProperty("application.repository.ticket.name");
		return new RepositoryRemoteBinding(restTemplate, repositoryUri, ticketName, userService);
	}

	@Bean
	public RepositoryRemoteBinding unauthenticatedRepositoryRemoteBinding(RestTemplate restTemplate) {
		String repositoryUri = environment.getRequiredProperty("application.repository.root.uri");
		return new RepositoryRemoteBinding(restTemplate, repositoryUri);
	}

	@Bean
	public RepositoryRemoteBinding requiringExplicitTicketRemoteBinding(RestTemplate restTemplate) {
		String repositoryUri = environment.getRequiredProperty("application.repository.root.uri");
		String ticketName = environment.getRequiredProperty("application.repository.ticket.name");
		return new RepositoryRemoteBinding(restTemplate, repositoryUri, ticketName);
	}

	@Bean
	public RepositoryRemoteBinding authenticationRemoteBinding(RestTemplate restTemplate) {
		String authenticationUri = environment.getRequiredProperty("application.authentication.repository.root.uri");
		return new RepositoryRemoteBinding(restTemplate, authenticationUri);
	}

}
