Description
===========
Owsi-Alfresco est un ensemble d'outils destinés à rendre plus rapide et maintenable les développements autour 
d'Alfresco 6.0.0.

Liens
=====
https://ci-jenkins.vitry.intranet/job/owsi-alfresco/job/owsi-alfresco/

FAQ
===
* Au moment du login, l'application indique qu'Alfresco a répondu 404 : 

Si vous utilisez Owsi-Alfresco depuis ses sources compilés dans Eclipse au lieu de le tirer par jar, 
il les fichiers XML générés dans target/classes ont peut-être disparu. 
Il faut lancer "mvn package" sur owsi-alfresco, puis raffraichir le workspace.  

* Dans Eclipse, si vous n'arrivez pas à ajouter un projet Share dans tomcat : 

Le projet n’apparaît pas dans la liste, car il ne veut pas de projet web en 3.1, juste en 3.0. 
Pour corriger : http://crunchify.com/how-to-fix-cannot-change-version-of-project-facet-dynamic-web-module-to-3-0-error-in-eclipse/

* Pour déployer :
mvn clean source:jar deploy -DargLine="" -Ddistribution=owsi-alfresco-release

Vous risquez d'avoir besoin de modifier votre `~/.m2/settings.xml`. Pour celà le fichier owsi-m2/settings.xml permet d'utiliser OWSI-alfresco

Nouveautés
==========

## depuis 0.8.2
* Classification gère si noeud supprimé entre temps
* Classification désactive cm:version, cm:auditable et owsi:classifiable policies 
* Swagger : modification de rednu généré
* Intégre AMP AOS
* /owsi/admin/configuration Renvoi configuration loggué au démarrage
* Log git.properties au démarrage.

## depuis 0.8.1
* owsi.classification.freemarker.models pour classifier les cas simple
* /owsi/classification/clearcache Pour vider les caches de classification
* /owsi/swagger.yaml

## depuis 0.8.0
* Migration ACS 6.0
* Supression des packages : 
  - owsi-alfresco-package-alfresco devient org.alfresco:alfresco-enterprise 
  - owsi-alfresco-package-share devient org.alfresco:share
* alfresco.module.version devient project.version
* NodeModelRepositoryService n'etends plus NodeServiceImpl
* delete() devient deleteNode()
* NodeModelRepositoryService utilise des NodeRef au lieu de NodeReference

## depuis 0.7.0
* Alfresco 5.2.4
* Chaos Monkeys

## depuis 0.6.0
* Alfresco 5.2.3
* Permet de demander highlight dans des recherches Solr.
* Alternate DownloadResponseHandler pour customiser comportement
* Génére un fichier git.properties dans les JAR/WAR

## depuis 0.5.0
* authorityModelService.getUser(userName, nodeScopeBuilder) --> authorityModelService.getCachedUser(userName)
* http://localhost:8080/alfresco/s/owsi/contentstoreexport.zip
* DownloadResponse : Téléchargement par chunk
* DownloadResponse : Watermark invisible
* Aspect owsi:deleteIfEmpty : efface un répertoire qui aurait l'aspect s'il devient vide
* Configuration logger coté Alfresco et applications
* LicenseService
* Batch search coté Alfresco
* reset version d'un module : owsi.reset-module-version.modules=owsi-alfresco-demo-alfresco-component:0.0.0
* reset password d'un utilisateur : owsi.reset-user-password.users=admin:admin123
* Plugin Maven de migration (MigrationMojo) : Permet de savoir si un patch doit être adapté à une nouvelle version d'Alfresco.
* PermissionRepositoryService.searchACL() 
* owsi-alfresco-repo-emailed2eml : Conversion des emails reçu en fichiers .eml

Migration
=========

## vers 0.8.0
* NodeModelRepositoryService utilise des NodeRef au lieu de NodeReference

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
