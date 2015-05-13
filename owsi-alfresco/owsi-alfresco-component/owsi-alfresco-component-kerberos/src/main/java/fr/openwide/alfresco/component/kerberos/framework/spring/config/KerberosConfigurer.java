package fr.openwide.alfresco.component.kerberos.framework.spring.config;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.kerberos.authentication.KerberosServiceAuthenticationProvider;
import org.springframework.security.kerberos.authentication.KerberosTicketValidator;
import org.springframework.security.kerberos.web.authentication.SpnegoAuthenticationProcessingFilter;
import org.springframework.security.kerberos.web.authentication.SpnegoEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.google.common.base.Optional;

/**
 * {@see org.springframework.security.config.annotation.web.configurers.X509Configurer}
 */
public class KerberosConfigurer<B extends HttpSecurityBuilder<B>> extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, B> {

	private Optional<AuthenticationEntryPoint> authenticationEntryPoint = Optional.absent();
	private Optional<String> forwardPage = Optional.absent();
	private KerberosTicketValidator kerberosTicketValidator;
	private UserDetailsService userDetailsService;

	@Override
	public void init(B http) throws Exception {
		// configure authentication entry point
		if (authenticationEntryPoint.isPresent()) {
			registerAuthenticationEntryPoint(http);
		}
		if (forwardPage.isPresent()) {
			registerUrlAuthorization(http);
		}
		// configure authentication provider
		KerberosServiceAuthenticationProvider provider = new KerberosServiceAuthenticationProvider();
		provider.setTicketValidator(kerberosTicketValidator);
		provider.setUserDetailsService(getUserDetailsService(http));
		http.authenticationProvider(postProcess(provider));
	}

	@Override
	public void configure(B http) throws Exception {
		// configure filter
		AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
		SpnegoAuthenticationProcessingFilter filter = new SpnegoAuthenticationProcessingFilter();
		filter.setAuthenticationManager(authenticationManager);
		http.addFilterBefore(postProcess(filter), BasicAuthenticationFilter.class);
	}

	public KerberosConfigurer<B> entryPoint() {
		return entryPoint((String) null);
	}

	public KerberosConfigurer<B> entryPoint(String forwardPage) {
		this.forwardPage = Optional.fromNullable(forwardPage);
		return entryPoint(new SpnegoEntryPoint(forwardPage));
	}

	public KerberosConfigurer<B> entryPoint(AuthenticationEntryPoint entryPoint) {
		authenticationEntryPoint = Optional.<AuthenticationEntryPoint>of(entryPoint);
		return this;
	}

	public KerberosConfigurer<B> userDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
		return this;
	}

	public KerberosConfigurer<B> kerberosTicketValidator(KerberosTicketValidator kerberosTicketValidator) {
		this.kerberosTicketValidator = kerberosTicketValidator;
		return this;
	}

	private UserDetailsService getUserDetailsService(B http) {
		if (userDetailsService == null) {
			userDetailsService(http.getSharedObject(UserDetailsService.class));
		}
		return postProcess(userDetailsService);
	}

	@SuppressWarnings("unchecked")
	private void registerAuthenticationEntryPoint(B http) {
		ExceptionHandlingConfigurer<B> exceptionHandling = http.getConfigurer(ExceptionHandlingConfigurer.class);
		if (exceptionHandling == null) {
			return;
		}
		exceptionHandling.authenticationEntryPoint(postProcess(authenticationEntryPoint.get()));
	}

	@SuppressWarnings("unchecked")
	private void registerUrlAuthorization(B http) {
		ExpressionUrlAuthorizationConfigurer<B> configurer = http.getConfigurer(ExpressionUrlAuthorizationConfigurer.class);
		if (configurer == null) {
			return;
		}
		configurer.getRegistry().antMatchers(forwardPage.get()).permitAll();
	}

}
