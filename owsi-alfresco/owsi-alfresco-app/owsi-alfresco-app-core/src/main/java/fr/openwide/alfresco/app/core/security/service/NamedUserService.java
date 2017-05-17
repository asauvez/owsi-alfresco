package fr.openwide.alfresco.app.core.security.service;

import org.springframework.security.core.Authentication;

import fr.openwide.alfresco.app.core.security.model.NamedUser;

public interface NamedUserService extends UserService {

	NamedUser getUser(Authentication authentication);

	NamedUser getCurrentUser();

}
