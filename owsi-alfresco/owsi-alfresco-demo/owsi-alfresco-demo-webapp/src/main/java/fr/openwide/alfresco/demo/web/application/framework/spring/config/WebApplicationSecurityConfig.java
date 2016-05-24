package fr.openwide.alfresco.demo.web.application.framework.spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import fr.openwide.alfresco.app.core.security.service.RepositoryAuthenticationUserDetailsService;
import fr.openwide.alfresco.app.core.security.service.impl.RepositoryAuthenticationProvider;
import fr.openwide.alfresco.app.web.security.authentication.RepositoryLogoutHandler;
import fr.openwide.alfresco.demo.web.application.business.ConnectionController;

@Configuration
@EnableWebSecurity
public class WebApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private RepositoryAuthenticationProvider repositoryAuthenticationProvider;

	@Autowired
	private RepositoryAuthenticationUserDetailsService userDetailsService;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
			.headers().disable()
			.authenticationProvider(repositoryAuthenticationProvider)
			.formLogin()
				.loginPage(ConnectionController.LOGIN_URL)
				.loginProcessingUrl("/j_spring_security_check")
				.usernameParameter("j_username")
				.passwordParameter("j_password")
				.defaultSuccessUrl("/")
				.failureUrl(ConnectionController.LOGIN_URL)
				.permitAll()
				.and()
			.logout()
				.logoutRequestMatcher(new AntPathRequestMatcher("/security/logout", "GET"))
				.logoutSuccessUrl("/")
				.and()
			.authorizeRequests()
				.antMatchers(ConnectionController.LOGIN_URL).permitAll()
				.antMatchers(HttpMethod.GET, "/security/logout").permitAll()
				.antMatchers("/static/**").permitAll()
				.anyRequest().authenticated()
				.and();
	}
	
	@Bean
	public RepositoryLogoutHandler repositoryLogoutHandler() {
		return new RepositoryLogoutHandler(userDetailsService);
	}

	@Bean // force registration of authenticationManager
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

}
