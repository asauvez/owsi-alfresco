package fr.openwide.alfresco.repo.dictionary.permission.service.impl;

import org.alfresco.enterprise.repo.authorization.AuthorizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class AuthorizationServiceHelperImpl extends AuthorizationServiceHelper {
	
	private final Logger LOGGER = LoggerFactory.getLogger(AuthorizationServiceHelperImpl.class);
	
	private AuthorizationService authorizationService;

	@Override
	protected void init(ApplicationContext applicationContext) {
		authorizationService = applicationContext.getBean(AuthorizationService.class);
	}
	
	@Override
	protected void internalDeauthorizeUser(String oldAuthorityName) {
		if (authorizationService.isAuthorized(oldAuthorityName)) {
			authorizationService.deauthorize(oldAuthorityName);
			LOGGER.warn(String.format("%s has been deauthorized", oldAuthorityName));
		} else {
			LOGGER.warn(String.format("failed to deauthorize: user %s was never authorized", oldAuthorityName));
		}
	}

}
