package fr.openwide.alfresco.app.web.framework.spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import fr.openwide.alfresco.app.web.security.authentication.KerberosPreAuthenticatedFilter;
import fr.openwide.alfresco.app.web.security.authentication.RepositoryPreAuthenticatedUserDetailsService;

@Configuration
public class AppWebKerberosSecurityConfig {

	@Autowired
	protected Environment environment;

	@Bean
	public KerberosPreAuthenticatedFilter kerberosPreAuthenticatedFilter(AuthenticationManager authenticationManager) {
		Boolean enabled = environment.getProperty("jaas.login.enabled", Boolean.class, false);
		String configEntryName = (enabled) ? environment.getRequiredProperty("jaas.login.configuration") : null;
		KerberosPreAuthenticatedFilter filter = new KerberosPreAuthenticatedFilter(authenticationManager, enabled, configEntryName);
		// do continue on unsuccessful authentication
		Boolean allowContinue = environment.getProperty("jaas.login.allowContinue", Boolean.class, true);
		filter.setContinueFilterChainOnUnsuccessfulAuthentication(allowContinue);
		// bypass uriPattern / header
		String bypassUriPattern = environment.getProperty("jaas.login.bypass.uriPattern");
		String bypassHeader = environment.getProperty("jaas.login.bypass.header");
		filter.setBypassUriPattern(bypassUriPattern);
		filter.setBypassHeader(bypassHeader);
		return filter;
	}

	@Bean
	public AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> repositoryPreAuthenticatedUserDetailsService() {
		return new RepositoryPreAuthenticatedUserDetailsService();
	}

	@Bean
	public PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider(
			AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> userDetailsService) {
		PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
		provider.setPreAuthenticatedUserDetailsService(userDetailsService);
		provider.setThrowExceptionWhenTokenRejected(true);
		return provider;
	}

}
