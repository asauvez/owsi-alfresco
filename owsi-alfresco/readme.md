Description
===========
Owsi-Alfresco est un ensemble d'outils destinés à rendre plus rapide et maintenable les développements autour 
d'Alfresco.

FAQ
===
* Au moment du login, l'application indique qu'Alfresco a répondu 404 : 

Si vous utilisez Owsi-Alfresco depuis ses sources compilés dans Eclipse au lieu de le tirer par jar, 
il les fichiers XML générés dans target/classes ont peut-être disparu. 
Il faut lancer "mvn package" sur owsi-alfresco, puis raffraichir le workspace.  

* Dans Eclipse, si vous n'arrivez pas à ajouter un projet Share dans tomcat : 

Le projet n’apparaît pas dans la liste, car il ne veut pas de projet web en 3.1, juste en 3.0. 
Pour corriger : http://crunchify.com/how-to-fix-cannot-change-version-of-project-facet-dynamic-web-module-to-3-0-error-in-eclipse/

Nouveautés
==========
## depuis 0.5.0
* authorityModelService.getUser(userName, nodeScopeBuilder) --> authorityModelService.getCachedUser(userName)
* http://localhost:8080/alfresco/s/owsi/contentstoreexport.zip
* DownloadResponse : Téléchargement par chunk
* DownloadResponse : Watermark invisible
* Aspect owsi:deleteIfEmpty : efface un répertoire qui aurait l'aspect s'il devient vide
* Configuration logger coté Alfresco et applications
* LicenseService
* Batch search coté Alfresco

Migration
=========

## vers 0.5.0
### Changement de paquets Maven
* owsi-alfresco-api-dictionary --> owsi-alfresco-api-core
* owsi-alfresco-component-model --> owsi-alfresco-api-core
* owsi-alfresco-repo-dictionary --> owsi-alfresco-repo-core
* owsi-alfresco-repo-remote --> owsi-alfresco-repo-core
* owsi-alfresco-parent-repo --> owsi-alfresco-parent-repo-component
* fr.openwide.alfresco.repository --> fr.openwide.alfresco.repo
* AppDictionaryServiceConfig --> AppCoreServiceConfig

### Authentification
* Il n'y a plus de PrincipalType. Si vous utilisiez PrincipalType.USER_DETAILS, vous continuez à déclarer un autre AuthenticationProvider que RepositoryAuthenticationProvider dans votre WebApplicationSecurityConfig.
* Vous pouvez aussi supprimer @Bean RepositoryAuthenticationProvider et @bean RepositoryAuthenticationUserDetailsService de votre CoreCommonSecurityConfig. Ils sont intégré dans AppCoreSecurityConfig.
* Le runAsUser() déconnecte l'utilisateur après coup si vous passez un username et non un UserDetails. Avant cela le faisait quand vous étiez en PrincipalType.USER_DETAILS.

### Divers
* RepositoryAuthority --> AuthorityReference
* RepositoryPermission --> PermissionReference
* RepositoryTicket --> TicketReference
* new AuthorityReference(String) --> AuthorityReference.authority(String)
* Restriction.toQuery() --> Restriction.toFtsQuery()
* com.google.common.base.Optional --> java.util.Optional
