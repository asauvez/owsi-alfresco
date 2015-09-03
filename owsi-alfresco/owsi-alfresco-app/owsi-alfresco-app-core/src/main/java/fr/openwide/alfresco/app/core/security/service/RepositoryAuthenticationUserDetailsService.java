package fr.openwide.alfresco.app.core.security.service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsService;

import fr.openwide.alfresco.api.core.authentication.model.RepositoryUser;

public interface RepositoryAuthenticationUserDetailsService extends AuthenticationUserDetailsService<UsernamePasswordAuthenticationToken>, UserDetailsService {

	void logout(Authentication authentication);
	
	void renewTicket(RepositoryUser repositoryUser);
}
