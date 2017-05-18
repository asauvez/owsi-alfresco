package fr.openwide.alfresco.app.core.security.model;

import org.springframework.security.core.userdetails.UserDetails;

import fr.openwide.alfresco.app.core.framework.spring.config.PrincipalTypeImportSelector;

/**
 * @see PrincipalTypeImportSelector
 */
public enum PrincipalType {
	
	/**
	 * Default mode. Alfresco is the main authentication mecanism.
	 * 
	 * The Principal is always of type NamedUser.
	 * 
	 * @see NamedUser
	 */
	NAMED_USER,

	/**
	 * The main authentication mecanism is not Alfresco. It may be users in database or any other systems.
	 * 
	 * The Principal is of type NamedUser only inside a runAs bloc.
	 * Outside, it may be of any child of UserDetails.
	 * 
	 * To call Alfresco, you have to be in a runAs bloc. When the runAs bloc is ended, the original authentication
	 * context is restored.
	 * 
	 * @see UserDetails
	 */
	USER_DETAILS,
}
