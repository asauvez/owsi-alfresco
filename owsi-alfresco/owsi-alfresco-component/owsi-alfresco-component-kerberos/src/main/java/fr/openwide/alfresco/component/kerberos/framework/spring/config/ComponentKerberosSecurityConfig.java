package fr.openwide.alfresco.component.kerberos.framework.spring.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.kerberos.authentication.KerberosServiceAuthenticationProvider;
import org.springframework.security.kerberos.authentication.KerberosTicketValidator;
import org.springframework.security.kerberos.authentication.sun.SunJaasKerberosTicketValidator;
import org.springframework.security.kerberos.web.authentication.SpnegoAuthenticationProcessingFilter;
import org.springframework.security.kerberos.web.authentication.SpnegoEntryPoint;

import fr.openwide.alfresco.component.kerberos.authentication.RealmAwareKerberosTicketValidator;

/**
 * A importer dans la configuration de l'application web
 * Nécessite :
 * - de définir un @Bean de type String nommé "kerberosEntryPointUrl"
 * - de définir un @Bean de type UserDetailsService avec un alias "preAuthenticatedUserDetailsService"
 * - de définir la propriété "application.kerberos.service.principal"
 * - de définir la propriété "application.kerberos.service.keyTab"
 * 
 * Optionnel :
 * - la propriété "application.kerberos.service.enabled" peut être changée à false (default: true)
 * - la propriété "application.kerberos.service.debug" peut être changée à false (default: true)
 *
 */
@Configuration
public class ComponentKerberosSecurityConfig {

	@Autowired
	@Qualifier("kerberosEntryPointUrl")
	private String entryPointUrl;

	@Autowired
	@Qualifier("preAuthenticatedUserDetailsService")
	private UserDetailsService userDetailsService;

	@Autowired
	private Environment environment;
	@Autowired
	private ResourceLoader resourceLoader;

	private static final Logger LOGGER = LoggerFactory.getLogger(ComponentKerberosSecurityConfig.class);

	@Bean
	public KerberosWebSecurityConfigurer kerberosConfigurer() {
		return new KerberosWebSecurityConfigurer();
	}

	@Bean
	public SpnegoEntryPoint spnegoEntryPoint() {
		return new SpnegoEntryPoint(entryPointUrl);
	}

	@Bean
	public SpnegoAuthenticationProcessingFilter spnegoAuthenticationProcessingFilter(AuthenticationManager authenticationManager) {
		SpnegoAuthenticationProcessingFilter filter = new SpnegoAuthenticationProcessingFilter();
		filter.setAuthenticationManager(authenticationManager);
		return filter;
	}

	@Bean
	public KerberosServiceAuthenticationProvider kerberosServiceAuthenticationProvider(KerberosTicketValidator kerberosTicketValidator) {
		KerberosServiceAuthenticationProvider provider = new KerberosServiceAuthenticationProvider();
		provider.setTicketValidator(kerberosTicketValidator);
		provider.setUserDetailsService(userDetailsService);
		return provider;
	}

	@Bean
	public SunJaasKerberosTicketValidator kerberosTicketValidator() {
		boolean enabled = environment.getProperty("application.kerberos.service.enabled", Boolean.class, true);
		SunJaasKerberosTicketValidator ticketValidator = new RealmAwareKerberosTicketValidator(enabled);
		if (enabled) {
			ticketValidator.setServicePrincipal(environment.getRequiredProperty("application.kerberos.service.principal"));
			ticketValidator.setKeyTabLocation(resourceLoader.getResource(environment.getRequiredProperty("application.kerberos.service.keyTab")));
			ticketValidator.setDebug(environment.getProperty("application.kerberos.service.debug", Boolean.class, true));
		} else {
			LOGGER.warn("Kerberos service authentication is disabled");
		}
		return ticketValidator;
	}

}
