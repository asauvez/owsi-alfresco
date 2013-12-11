package fr.openwide.alfresco.app.core.security.service;

import org.springframework.security.core.Authentication;

import fr.openwide.alfresco.app.core.security.model.BusinessUser;

public interface UserService {

	BusinessUser getUser(Authentication authentication);

	BusinessUser getCurrentUser();
	String getCurrentUserId();

}
