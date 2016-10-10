package fr.openwide.alfresco.demo.core.application.security.model;

import fr.openwide.core.jpa.security.model.CorePermissionConstants;

public interface BusinessPermissionConstants {
/*
	String ROLE_GESTIONNAIRE       = "hasAuthority('GROUP_" + BusinessAuthorityConstants.GESTIONNAIRE + "')";
	String ROLE_INSTRUCTEUR        = "hasAuthority('GROUP_" + BusinessAuthorityConstants.INSTRUCTEUR + "')";
	String ROLE_MEDECIN            = "hasAuthority('GROUP_" + BusinessAuthorityConstants.MEDECIN + "')";
	String ROLE_PSYCHOLOGUE        = "hasAuthority('GROUP_" + BusinessAuthorityConstants.PSYCHOLOGUE + "')";
	String ROLE_TRAVAILLEUR_SOCIAL = "hasAuthority('GROUP_" + BusinessAuthorityConstants.TRAVAILLEUR_SOCIAL + "')";
	String ROLE_OBSERVATEUR        = "hasAuthority('GROUP_" + BusinessAuthorityConstants.OBSERVATEUR + "')";

	String ROLE_UTILISATEUR = 
			  "    hasAuthority('GROUP_" + BusinessAuthorityConstants.GESTIONNAIRE + "')"
			+ " or hasAuthority('GROUP_" + BusinessAuthorityConstants.INSTRUCTEUR + "')"
			+ " or hasAuthority('GROUP_" + BusinessAuthorityConstants.MEDECIN + "')"
			+ " or hasAuthority('GROUP_" + BusinessAuthorityConstants.PSYCHOLOGUE + "')"
			+ " or hasAuthority('GROUP_" + BusinessAuthorityConstants.TRAVAILLEUR_SOCIAL + "')"
			+ " or hasAuthority('GROUP_" + BusinessAuthorityConstants.OBSERVATEUR + "')";
	String ROLE_GESTIONNAIRE_OU_INSTRUCTEUR = 
			  "    hasAuthority('GROUP_" + BusinessAuthorityConstants.GESTIONNAIRE + "')"
			+ " or hasAuthority('GROUP_" + BusinessAuthorityConstants.INSTRUCTEUR + "')";
	String ROLE_GESTIONNAIRE_OU_MEDECIN = 
			  "    hasAuthority('GROUP_" + BusinessAuthorityConstants.GESTIONNAIRE + "')"
			+ " or hasAuthority('GROUP_" + BusinessAuthorityConstants.MEDECIN + "')";*/

	String READ = CorePermissionConstants.READ;
	String WRITE = CorePermissionConstants.WRITE;
	String CREATE = CorePermissionConstants.CREATE;
	String DELETE = CorePermissionConstants.DELETE;
	String ADMINISTRATION = CorePermissionConstants.ADMINISTRATION;

}
