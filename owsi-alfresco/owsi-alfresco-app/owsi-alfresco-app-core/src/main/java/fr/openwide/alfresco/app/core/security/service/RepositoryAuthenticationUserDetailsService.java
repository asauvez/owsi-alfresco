package fr.openwide.alfresco.app.core.security.service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import fr.openwide.alfresco.app.core.security.model.NamedUser;

public interface RepositoryAuthenticationUserDetailsService extends AuthenticationUserDetailsService<UsernamePasswordAuthenticationToken>, UserDetailsService {

	@Override
	NamedUser loadUserByUsername(String username) throws UsernameNotFoundException;
	
	void logout(NamedUser user);
	
	void renewTicket(NamedUser user);
}
