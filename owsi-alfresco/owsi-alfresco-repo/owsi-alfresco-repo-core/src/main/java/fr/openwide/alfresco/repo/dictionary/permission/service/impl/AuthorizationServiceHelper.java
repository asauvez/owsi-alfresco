package fr.openwide.alfresco.repo.dictionary.permission.service.impl;

import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/** 
 * Permet de faire tourner aussi bien en community qu'en entreprise 
 * 
 * Pour que la fonctionnalité de désactivation d'utilisateur soit disponible, 
 * il faut compiler avec le profile enterprise (mvn -Penterprise)
 * et tirer le module owsi-alfresco-repo-core-enterprise au lieu de owsi-alfresco-repo-core.
 */
public class AuthorizationServiceHelper {
	
	private final Logger LOGGER = LoggerFactory.getLogger(AuthorizationServiceHelper.class);
	
	private AuthorizationServiceHelper realAuthorizationServiceHelper;
	
	protected AuthorizationServiceHelper() {}
	public AuthorizationServiceHelper(ApplicationContext applicationContext) {
		init(applicationContext);
	}
	
	protected void init(ApplicationContext applicationContext) {
		try {
			Class<?> clazz = Class.forName(AuthorizationServiceHelper.class.getName() + "Impl");
			AuthorizationServiceHelper authorizationServiceHelper = (AuthorizationServiceHelper) clazz.getConstructor().newInstance();
			authorizationServiceHelper.init(applicationContext);
			this.realAuthorizationServiceHelper = authorizationServiceHelper; 
		} catch (NoClassDefFoundError e) {
			LOGGER.info("Version community");
			this.realAuthorizationServiceHelper = this;
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public final void deauthorizeUser(String oldAuthorityName) {
		realAuthorizationServiceHelper.internalDeauthorizeUser(oldAuthorityName);
	}
	protected void internalDeauthorizeUser(@SuppressWarnings("unused") String oldAuthorityName) {
		throw new UnsupportedOperationException("deauthorizeUser n'est pas utile en version Community. "
				+ "Si vous êtes en version Enterprise, compiler Owsi-Alfresco avec -Penterprise et tirer owsi-alfresco-repo-core-enterprise");
	}

}
