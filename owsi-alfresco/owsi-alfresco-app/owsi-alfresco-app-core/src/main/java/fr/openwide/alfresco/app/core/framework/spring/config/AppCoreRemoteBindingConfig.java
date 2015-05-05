package fr.openwide.alfresco.app.core.framework.spring.config;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.openwide.alfresco.app.core.AlfrescoAppCorePackage;
import fr.openwide.alfresco.app.core.node.binding.ByteArrayRepositoryContentSerializer;
import fr.openwide.alfresco.app.core.node.binding.InputStreamRepositoryContentSerializer;
import fr.openwide.alfresco.app.core.node.binding.MultipartFileRepositoryContentSerializer;
import fr.openwide.alfresco.app.core.node.binding.ReaderRepositoryContentSerializer;
import fr.openwide.alfresco.app.core.node.binding.StringRepositoryContentSerializer;
import fr.openwide.alfresco.app.core.node.binding.TempFileRepositoryContentSerializer;
import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryRemoteBinding;
import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryRemoteExceptionHandler;
import fr.openwide.alfresco.app.core.security.service.RepositoryTicketProvider;
import fr.openwide.alfresco.repository.api.node.binding.RepositoryContentSerializationComponent;
import fr.openwide.alfresco.repository.api.node.binding.RepositoryContentSerializer;

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
public class AppCoreRemoteBindingConfig {

	@Autowired
	private Environment environment;

	@Bean
	@Primary
	public RepositoryRemoteBinding userAwareRepositoryRemoteBinding(RestTemplate restTemplate, 
			RepositoryContentSerializationComponent serializationComponent, RepositoryTicketProvider ticketProvider) {
		String repositoryUri = environment.getRequiredProperty("application.repository.root.uri");
		String ticketName = environment.getRequiredProperty("application.repository.ticket.name");
		return new RepositoryRemoteBinding(restTemplate, serializationComponent, repositoryUri, ticketName, null, ticketProvider);
	}

	@Bean
	public RepositoryRemoteBinding unauthenticatedRepositoryRemoteBinding(RestTemplate restTemplate, 
			RepositoryContentSerializationComponent serializationComponent) {
		String repositoryUri = environment.getRequiredProperty("application.repository.root.uri");
		return new RepositoryRemoteBinding(restTemplate, serializationComponent, repositoryUri);
	}

	@Bean
	public RepositoryRemoteBinding requiringExplicitTicketRemoteBinding(RestTemplate restTemplate, 
			RepositoryContentSerializationComponent serializationComponent) {
		String repositoryUri = environment.getRequiredProperty("application.repository.root.uri");
		String ticketName = environment.getRequiredProperty("application.repository.ticket.name");
		return new RepositoryRemoteBinding(restTemplate, serializationComponent, repositoryUri, ticketName);
	}

	@Bean
	public RepositoryRemoteBinding authenticationRemoteBinding(RestTemplate restTemplate, 
			RepositoryContentSerializationComponent serializationComponent) {
		String authenticationUri = environment.getRequiredProperty("application.authentication.repository.root.uri");
		return new RepositoryRemoteBinding(restTemplate, serializationComponent, authenticationUri);
	}

	@Bean
	public RestTemplate remoteRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new RepositoryRemoteExceptionHandler());
		// do not buffer request objects (not to run out of memory on file upload...)
		((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setBufferRequestBody(false);
		return restTemplate;
	}

	@Bean
	public RepositoryContentSerializationComponent serializationComponent() {
		Map<Class<?>, RepositoryContentSerializer<?>> serializersByClass = new HashMap<>();
		serializersByClass.put(String.class, StringRepositoryContentSerializer.INSTANCE);
		serializersByClass.put(byte[].class, ByteArrayRepositoryContentSerializer.INSTANCE);
		serializersByClass.put(File.class, TempFileRepositoryContentSerializer.INSTANCE);
		serializersByClass.put(MultipartFile.class, MultipartFileRepositoryContentSerializer.INSTANCE);
		serializersByClass.put(InputStream.class, InputStreamRepositoryContentSerializer.INSTANCE);
		serializersByClass.put(Reader.class, ReaderRepositoryContentSerializer.INSTANCE);
		
		return new RepositoryContentSerializationComponent(
				new ObjectMapper(), 
				serializersByClass, 
				ByteArrayRepositoryContentSerializer.INSTANCE);
	}
	
}
