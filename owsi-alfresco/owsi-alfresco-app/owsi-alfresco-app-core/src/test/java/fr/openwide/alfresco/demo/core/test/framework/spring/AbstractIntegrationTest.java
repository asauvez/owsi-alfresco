package fr.openwide.alfresco.demo.core.test.framework.spring;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import fr.openwide.alfresco.app.core.security.service.RepositoryAuthenticationUserDetailsService;
import fr.openwide.alfresco.demo.core.test.framework.spring.config.IntegrationTestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=IntegrationTestConfig.class)
@TestExecutionListeners(DependencyInjectionTestExecutionListener.class)
@IfProfileValue(name="integration-test", value="true")
public abstract class AbstractIntegrationTest {

	@Autowired
	private RepositoryAuthenticationUserDetailsService userDetailsService;
	
	private Authentication testAuthentication = null;
	
	public String getTestUsername() {
		return "admin";
	}
	
	@Before
	public void loginTestUser() {
		loginUser(getTestUsername());
	}
	public void loginUser(String username) {
		logoutUser();
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		testAuthentication = new UsernamePasswordAuthenticationToken(userDetails, null);
		SecurityContextHolder.getContext().setAuthentication(testAuthentication);
	}
	
	@After
	public void logoutUser() {
		if (testAuthentication != null) {
			SecurityContextHolder.clearContext();
			userDetailsService.logout(testAuthentication);
			testAuthentication = null;
		}
	}
}
