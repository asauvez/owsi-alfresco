package fr.openwide.alfresco.app.core.framework.spring.config;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.openwide.alfresco.api.core.node.binding.content.NodeContentSerializationComponent;
import fr.openwide.alfresco.api.core.node.binding.content.NodeContentSerializer;
import fr.openwide.alfresco.api.core.node.binding.content.serializer.ByteArrayRepositoryContentSerializer;
import fr.openwide.alfresco.app.core.node.binding.MultipartFileRepositoryContentSerializer;
import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryRemoteBinding;
import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryRemoteExceptionHandler;
import fr.openwide.alfresco.app.core.security.service.TicketReferenceProvider;

@Configuration
public class AppCoreRemoteBindingConfig {

	@Autowired
	private Environment environment;

	@Bean
	@Primary
	public RepositoryRemoteBinding userAwareRepositoryRemoteBinding(TicketReferenceProvider ticketProvider) {
		String repositoryUri = environment.getRequiredProperty("application.repository.root.uri");
		String ticketName = environment.getRequiredProperty("application.repository.ticket.name");
		return new RepositoryRemoteBinding(remoteRestTemplate(), serializationComponent(), repositoryUri, ticketName, null, ticketProvider);
	}

	@Bean
	public RepositoryRemoteBinding unauthenticatedRepositoryRemoteBinding() {
		String repositoryUri = environment.getRequiredProperty("application.repository.root.uri");
		return new RepositoryRemoteBinding(remoteRestTemplate(), serializationComponent(), repositoryUri);
	}

	@Bean
	public RepositoryRemoteBinding requiringExplicitTicketRemoteBinding() {
		String repositoryUri = environment.getRequiredProperty("application.repository.root.uri");
		String ticketName = environment.getRequiredProperty("application.repository.ticket.name");
		return new RepositoryRemoteBinding(remoteRestTemplate(), serializationComponent(), repositoryUri, ticketName);
	}

	@Bean
	public RepositoryRemoteBinding authenticationRemoteBinding() {
		String authenticationUri = environment.getRequiredProperty("application.authentication.repository.root.uri");
		return new RepositoryRemoteBinding(remoteRestTemplate(), serializationComponent(), authenticationUri);
	}

	@Bean
	public RepositoryRemoteBinding shareRemoteBinding(TicketReferenceProvider ticketProvider) {
		String repositoryUri = environment.getRequiredProperty("application.share.root.uri");
		String ticketName = environment.getRequiredProperty("application.share.ticket.name");
		return new RepositoryRemoteBinding(remoteRestTemplate(), serializationComponent(), repositoryUri, null, ticketName, ticketProvider);
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
	public NodeContentSerializationComponent serializationComponent() {
		Map<Class<?>, NodeContentSerializer<?>> serializersByClass = NodeContentSerializationComponent.getDefaultSerializersByClass();
		serializersByClass.put(MultipartFile.class, MultipartFileRepositoryContentSerializer.INSTANCE);

		return new NodeContentSerializationComponent(
				new ObjectMapper(), 
				serializersByClass, 
				ByteArrayRepositoryContentSerializer.INSTANCE);
	}
	
}
