package fr.openwide.alfresco.app.core.security.model;

import fr.openwide.alfresco.app.core.framework.spring.config.PrincipalTypeImportSelector;

/**
 * @see PrincipalTypeImportSelector
 */
public enum PrincipalType {
	
	/**
	 * Default mode. Alfresco is the main authentication mecanism.
	 */
	NAMED_USER,

	/**
	 * The main authentication mecanism is not Alfresco. It may be users in database or any other systems.
	 * 
	 * To call Alfresco, you have to be in a runAs bloc. When the runAs bloc is ended, the original authentication
	 * context is restored.
	 */
	USER_DETAILS,
}
