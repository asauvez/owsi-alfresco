package fr.openwide.alfresco.app.core.framework.spring.config;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.intercept.RunAsImplAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.openwide.alfresco.api.core.node.binding.content.NodeContentSerializationComponent;
import fr.openwide.alfresco.api.core.node.binding.content.NodeContentSerializer;
import fr.openwide.alfresco.api.core.node.binding.content.serializer.ByteArrayRepositoryContentSerializer;
import fr.openwide.alfresco.app.core.authentication.service.AuthenticationService;
import fr.openwide.alfresco.app.core.authentication.service.impl.AuthenticationServiceImpl;
import fr.openwide.alfresco.app.core.node.binding.MultipartFileRepositoryContentSerializer;
import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryRemoteBinding;
import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryRemoteExceptionHandler;
import fr.openwide.alfresco.app.core.security.service.RepositoryAuthenticationUserDetailsService;
import fr.openwide.alfresco.app.core.security.service.TicketReferenceProvider;
import fr.openwide.alfresco.app.core.security.service.RunAsUserManager;
import fr.openwide.alfresco.app.core.security.service.UserService;
import fr.openwide.alfresco.app.core.security.service.impl.RepositoryAuthenticationProvider;
import fr.openwide.alfresco.app.core.security.service.impl.RepositoryAuthenticationUserDetailsServiceImpl;
import fr.openwide.alfresco.app.core.security.service.impl.RunAsUserManagerImpl;
import fr.openwide.alfresco.app.core.security.service.impl.UserServiceImpl;

@Configuration
public class AppCoreSecurityConfig {

	private static final String RUN_AS_SHARED_KEY = UUID.randomUUID().toString();

	@Autowired private Environment environment;

	@Autowired(required=false)
	@Qualifier(AppCorePermissionConfigurerAdapter.LOGIN_TIME_ROLE_HIERARCHY)
	public RoleHierarchy loginTimeRoleHierarchy;
	
	@Bean
	public AuthenticationService authenticationService() {
		String authenticationHeader = environment.getRequiredProperty("application.authentication.repository.header.name");
		return new AuthenticationServiceImpl(
				unauthenticatedRepositoryRemoteBinding(), 
				requiringExplicitTicketRemoteBinding(),
				authenticationRemoteBinding(), 
				authenticationHeader);
	}
	
	@Bean
	public RepositoryAuthenticationUserDetailsService repositoryAuthenticationUserDetailsService() {
		return new RepositoryAuthenticationUserDetailsServiceImpl(authenticationService(), loginTimeRoleHierarchy);
	}

	/**
	 * Returns info about the current user.
	 * 
	 * The principal is a NamedUser when we are inside a runAs or when using PrincipalType.NAMED_USER.
	 * Outside runAs, Principal is also used to evaluate permission.
	 */
	@Bean
	public UserService userService() {
		return new UserServiceImpl();
	}
	
	@Bean
	public TicketReferenceProvider ticketProvider() {
		return new TicketReferenceProvider(userService(), repositoryAuthenticationUserDetailsService());
	}

	/**
	 * Provider to allow Spring MVC to authenticate with Alfresco.
	 */
	@Bean
	public RepositoryAuthenticationProvider repositoryAuthenticationProvider() {
		return new RepositoryAuthenticationProvider();
	}
	
	@Bean
	public RunAsImplAuthenticationProvider runAsAuthenticationProvider() {
		RunAsImplAuthenticationProvider provider = new RunAsImplAuthenticationProvider();
		provider.setKey(runAsSharedKey());
		return provider;
	}
	
	@Bean
	public RunAsUserManager runAsUserManager(AuthenticationManager authenticationManager) {
		RunAsUserManagerImpl manager = new RunAsUserManagerImpl(
				authenticationManager, 
				repositoryAuthenticationUserDetailsService(), 
				userService());
		manager.setKey(runAsSharedKey());
		return manager;
	}

	protected String runAsSharedKey() {
		return RUN_AS_SHARED_KEY;
	}

	// ----- RepositoryRemoteBinding ---------------------------------------------------------
	
	@Bean
	@Primary
	public RepositoryRemoteBinding userAwareRepositoryRemoteBinding() {
		String repositoryUri = environment.getRequiredProperty("application.repository.root.uri");
		String ticketName = environment.getRequiredProperty("application.repository.ticket.name");
		return new RepositoryRemoteBinding(remoteRestTemplate(), serializationComponent(), repositoryUri, ticketName, null, ticketProvider());
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
	public RepositoryRemoteBinding shareRemoteBinding() {
		String repositoryUri = environment.getRequiredProperty("application.share.root.uri");
		String ticketName = environment.getRequiredProperty("application.share.ticket.name");
		return new RepositoryRemoteBinding(remoteRestTemplate(), serializationComponent(), repositoryUri, null, ticketName, ticketProvider());
	}

	@Bean
	public RestTemplate remoteRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new RepositoryRemoteExceptionHandler());
		// do not buffer request objects (not to run out of memory on file upload...)
		SimpleClientHttpRequestFactory requestFactory = (SimpleClientHttpRequestFactory) restTemplate.getRequestFactory();
		requestFactory.setBufferRequestBody(false);
		
		int connectTimeout = environment.getRequiredProperty("application.repository.connectTimeout.ms", Integer.class);
		requestFactory.setConnectTimeout(connectTimeout);
		
		int readTimeout = environment.getRequiredProperty("application.repository.readTimeout.ms", Integer.class);
		requestFactory.setReadTimeout(readTimeout);
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
