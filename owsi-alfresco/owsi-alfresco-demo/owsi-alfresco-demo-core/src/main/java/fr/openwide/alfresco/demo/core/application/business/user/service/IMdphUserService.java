package fr.openwide.alfresco.demo.core.application.business.user.service;

import org.springframework.security.core.userdetails.UserDetails;

import com.google.common.base.Optional;


/**
 * @author Alexandre FIEVEE
 *
 */
public interface IMdphUserService {

	static final String PROFILE_I18N_KEY_PREFIX = "application.user-profile.";
	
	//List<MdphUserProfile> getCurrentUserProfiles();
	
	/**
	 * Retourne le login de l'utilisateur courant
	 * @return le login de l'utilisateur courant
	 */
	String getCurrentUserUsername();
	
	String getCurrentUserFirstName();
	
	String getCurrentUserLastName();
	
	String getCurrentUserFullName();
	
	Optional<UserDetails> getCurrentUser();
	
//	Map<String, List<MdphUser>> getInstructeurs();

	boolean isAdmin(UserDetails user);
	
}
