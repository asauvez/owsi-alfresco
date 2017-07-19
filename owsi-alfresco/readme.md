Avec owsi-alfresco 0.3.0, je n'arrivais plus à ajouter des projets Share dans tomcat. Le projet n’apparaît pas dans la liste, car il ne veut pas de projet web en 3.1, juste en 3.0. 
Pour corriger : http://crunchify.com/how-to-fix-cannot-change-version-of-project-facet-dynamic-web-module-to-3-0-error-in-eclipse/

# Nouveautés
## depuis 0.5.0
* authorityModelService.getUser(userName, nodeScopeBuilder) --> authorityModelService.getCachedUser(userName)
* http://localhost:8080/alfresco/s/owsi/contentstoreexport.zip
* DownloadResponse : Téléchargement par chunk
* DownloadResponse : Watermark invisible

# Migration

## vers 0.5.0
### Authentification
* Il n'y a plus de PrincipalType. Si vous utilisiez PrincipalType.USER_DETAILS, vous continuez à déclarer un autre AuthenticationProvider que RepositoryAuthenticationProvider dans votre WebApplicationSecurityConfig.
* Vous pouvez aussi supprimer @bean RepositoryAuthenticationProvider et @bean RepositoryAuthenticationUserDetailsService de votre CoreCommonSecurityConfig. Ils sont intégré dans AppCoreSecurityConfig.
* Le runAsUser() déconnecte l'utilisateur après coup si vous passez un username et non un UserDetails. Avant cela le faisait quand vous étiez en PrincipalType.USER_DETAILS.

### Divers
* new RepositoryAuthority(String) --> RepositoryAuthority.authority(String)
* Restriction.toQuery() --> Restriction.toFtsQuery()
* com.google.common.base.Optional --> java.util.Optional
