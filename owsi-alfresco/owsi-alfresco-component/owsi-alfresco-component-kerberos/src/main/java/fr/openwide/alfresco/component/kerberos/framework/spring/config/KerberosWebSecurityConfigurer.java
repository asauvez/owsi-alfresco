package fr.openwide.alfresco.component.kerberos.framework.spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.kerberos.authentication.KerberosServiceAuthenticationProvider;
import org.springframework.security.kerberos.web.authentication.SpnegoAuthenticationProcessingFilter;
import org.springframework.security.kerberos.web.authentication.SpnegoEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class KerberosWebSecurityConfigurer {

	@Autowired
	private SpnegoEntryPoint spnegoEntryPoint;
	@Autowired
	private SpnegoAuthenticationProcessingFilter spnegoAuthenticationProcessingFilter;
	@Autowired
	private KerberosServiceAuthenticationProvider kerberosServiceAuthenticationProvider;

	public void configure(HttpSecurity http) throws Exception {
		http.exceptionHandling()
				.authenticationEntryPoint(spnegoEntryPoint)
				.and()
			.addFilterBefore(spnegoAuthenticationProcessingFilter, BasicAuthenticationFilter.class);
	}

	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(kerberosServiceAuthenticationProvider);
	}

}
