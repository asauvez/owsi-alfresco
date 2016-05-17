package fr.openwide.alfresco.demo.core.application.security.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.userdetails.UserDetails;

import fr.openwide.alfresco.app.core.security.service.impl.UserAwarePermissionEvaluator;
import fr.openwide.alfresco.demo.core.application.security.model.BusinessPermission;

/**
 * @author Alexandre FIEVEE
 *
 */
public class BusinessPermissionEvaluator extends UserAwarePermissionEvaluator {

	private static final Logger LOGGER = LoggerFactory.getLogger(BusinessPermissionEvaluator.class);
	
	@Override
	protected boolean hasPermission(UserDetails user, Object targetDomainObject, Permission permission) {
		
		if (user == null) {
			LOGGER.debug("Aucun utilisateur connecté");
			return false;
		}
		
		boolean notImplementedPermission = false;
		/*
		if (targetDomainObject instanceof Tache) {
			Tache t = (Tache) targetDomainObject;
			if (BusinessPermission.DELETE.equals(permission)) {
				// Il faut être utilisateur et être l'auteur de la tâche, ou alors il faut être gestionnaire
				if ( (BusinessAuthorityConstants.isUtilisateur(user) && t.isAuteur(user.getUsername()))
						|| BusinessAuthorityConstants.isGestionnaire(user) ) {
					return true;
				}
			} else {
				notImplementedPermission = true;
			}
		} else if (targetDomainObject instanceof Bannette) {
			if (BusinessPermission.READ.equals(permission)) {
				// La restriction est faite lors de la recherche des bannettes, qui filtre les résultats sur les permissions
				return true;
			} else {
				notImplementedPermission = true;
			}
		}
		*/
		
		String targetDomainObjectClassName = targetDomainObject.getClass().getName();
		if (notImplementedPermission) {
			String message = "La permission " + permission.toString() + " n'est pas implémentée pour les objets du type " + targetDomainObjectClassName;
			LOGGER.error(message);
			throw new IllegalStateException(message);
		}
		
		String message = "L'objet " + targetDomainObject.toString() + " du type " + targetDomainObjectClassName + " n'est pas supporté";
		LOGGER.error(message);
		throw new IllegalStateException(message);
		
	}

}
