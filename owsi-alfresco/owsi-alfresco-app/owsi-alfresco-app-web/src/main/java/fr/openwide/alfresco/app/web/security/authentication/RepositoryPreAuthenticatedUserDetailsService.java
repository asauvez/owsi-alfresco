package fr.openwide.alfresco.app.web.security.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import fr.openwide.alfresco.app.core.security.service.impl.RepositoryAuthenticationUserDetailsService;

@Component
@Deprecated
public class RepositoryPreAuthenticatedUserDetailsService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

	@Autowired
	private RepositoryAuthenticationUserDetailsService repositoryAuthenticationUserDetailsService;

	@Override
	public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) {
		return repositoryAuthenticationUserDetailsService.loadUserByUsername((String) token.getPrincipal());
	}

}
