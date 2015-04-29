package fr.openwide.alfresco.app.web.framework.spring.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import fr.openwide.alfresco.app.web.security.authentication.RepositoryLogoutHandler;

@Deprecated
public class AppWebSecurityConfigurationSupport {

	private static final String DEFAULT_LOGOUT_SUCCESS_URL = "/";
	private static final String DEFAULT_LOGOUT_PATH = "/security/logout";

	@Autowired
	protected Environment environment;

	@Bean
	public AuthenticationManager authenticationManager() {
		List<AuthenticationProvider> providers = new ArrayList<>();
		addAuthenticationProviders(providers);
		return new ProviderManager(providers);
	}

	protected void addAuthenticationProviders(List<AuthenticationProvider> authenticationProviders) {
		// override as needed
	}

	@Bean
	public RepositoryLogoutHandler repositoryLogoutHandler() {
		return new RepositoryLogoutHandler();
	}

	@Bean
	public LogoutFilter repositoryAwareLogoutFilter(RepositoryLogoutHandler repositoryLogoutHandler) {
		String logoutSuccessUrl = environment.getProperty("application.authentication.logout.success.url", DEFAULT_LOGOUT_SUCCESS_URL);
		LogoutFilter logoutFilter = new LogoutFilter(logoutSuccessUrl, new SecurityContextLogoutHandler(), repositoryLogoutHandler);
		String logoutPath = environment.getProperty("application.authentication.logout.path", DEFAULT_LOGOUT_PATH);
		logoutFilter.setFilterProcessesUrl(logoutPath);
		return logoutFilter;
	}

}
