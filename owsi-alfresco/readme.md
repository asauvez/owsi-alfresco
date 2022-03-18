Description
===========
Owsi-Alfresco est un ensemble d'outils destinés à rendre plus rapide et maintenable les développements autour 
d'Alfresco 6.0.0.


Liens
=====
https://ci-jenkins.vitry.intranet/job/owsi-alfresco/job/owsi-alfresco/
https://sonar.vitry.intranet/dashboard?id=fr.openwide.alfresco%3Aowsi-alfresco

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

* Pour livrer une nouvelle version
mvn gitflow:release

Nouveautés
==========
## depuis 0.9.3
 * Fix ChildAspectServiceImpl sur revert ancienne version
 * Fix encoding swagger

## depuis 0.9.2
 * AlfrescoUrlService : Fix URL dossier Share
 * SearchQueryBuilder: maxPermissionChecks/maxPermissionCheckTimeMillis
 * Une partie de repo-core est déplacé dans app-component. Utile uniquement pour les projets faisant des appels distants.
 * ClassificationService: Deprecie l'utilisation de BusinessNode
 * @GenerateJavaModel: pour générer le modèle en Java.
 * Suppression de l'enforcer dans owsi-alfresco-parent-app

## depuis 0.8.9
Release du 05/12/2021
 * Ne dépend plus de owsi-core, sauf pour la partie Spring MVC (owsi-alfresco-app)
 * AlfrescoGlobalProperties : Permet d'accéder à la configuration.
 * UniqueNameService : Fix en cas de renommage d'un document qui a des secondary parents
 * aps-appInGit-maven-plugin : Permet de stocker les processus APS dans le projet.
 * TreeAspect : Copie aussi les properties des mandatory-aspects.
 * TreeAspect : Permet de déclarer des aspects avec owsi.treeaspect.register.
 * TreeAspect : N'hérite les métas que pour les childs assoc primary.
 * /owsi/alfresco.log : Retourne les dernières lignes de alfresco.log.
 * ClassificationBuilder.hasPropertiesChanged() : Pour savoir si une propriété à changer. 
 * Classification : ReclassifyParams
 * RunAtEveryLaunchPatch : patch lancé à chaque démarrage
 * AlfrescoUrlService : Pour obtenir les URL dans Share ou Content-App.
 * @GenerateWebscript : Permet de déclarer un cache.
 * @GenerateBootstrapModel : importViews pour importer des users, datalist
 * @GenerateXxx : Un seul fichier de context Spring /alfresco/extension/owsi-generator-context.xml

## depuis 0.8.8
Release du 05/07/2021
 * Solr Audit: pour savoir combien de fichier existe dans le repo.
 * UniqueNameRepositoryService : Gestion des noms lors des déplacements / renommage / classifications
 * ConstraintValues WS : Retourne les valeurs possibles pour un champs donné d'après les contraintes ou d'après une DataList.
 * Supprime owsi-alfresco-component-query jamais utilisé.
 * /owsi/batch/replacePropertyValue Pour remplacer une valeur par une autre dans une propriété
 * ChildAspectService : Affecte un aspect/type aux enfants des dossiers d'un aspect/type donné.

## depuis 0.8.7 Release du 09/02/2021
 * Affichage des scheduled jobs dans la console admin Alfresco
 * Fix pom si utilisation du SDK4-plus comme parent

## depuis 0.8.6
 * Classification: fix si une valeur était à null et ne l'est plus.

## depuis 0.8.5
 * Alfresco 6.2.1
 * Classification :
 	* N'appelle pas moveNode() si le document est déjà dans le bon dossier.
 	* rootFolderNamedPath() : Plus besoin de devoir voir les dossiers intermédiaires.
 	* uniqueName() sur plusieurs destinations.
	* Freemarker policies fait à la fin de l'initialisation pour avoir tous les modèles
	* owsi.classification.createSubFolderInInnerTransaction par défaut à false
 * AbstractPolicyService pour simplifier la mise en place de policy.
 * bindAssociationBehaviour() pour écouter createChild
 * ADF : Plugin Maven pour démarrer projet
 * ContentStoreExport : Fix totalVolume
 * owsi-alfresco-parent-sdk4 et owsi-alfresco-parent-sdk4-plus : Parents pour les dév sur une base SDK4.
 * Déplace alfresco-global du module dans repository.properties pour permettre à d'autres modules de surcharger des valeurs
 * Fix RestrictionBuilder.eq() pour les dates.
 * Ajout de RepositoryAccessControl.isInherited()
 * @GenerateService, @GeneratePatch, @GenerateBootstrapModel et @GenerateCron
 * ThresholdBuffer pour stocker des fichiers temporaires
 * BootstrapService dans core au lieu de module

## depuis 0.8.4
 * ContentStoreExport.exportVersions et newTransactionEveryDepth
 * owsi.classification.createSubFolderInInnerTransaction
 * owsi.classification.addClassificationDate
 * /alfresco/s/owsi/request-debug pour connaitre les headers.
 * Treeaspect : module de copie d'aspect avec les métadata aux enfants
 * owis-alfresco-test : Pour faire des tests d'appel distant.

## depuis 0.8.3
* MigrationMojo se base sur dependencies et plus sur le contenu du .m2.
* Pour surcharger un fichier présent dans une dépendance:
   * mvn package -Dowsi.migration.overrideFile=index.jsp
   * mvn package -Dowsi.migration.overrideContent=".alf-fullwindow .sticky-footer"s
* Remplace jgitflow par gitflow
* Module gestion des groupes de site par DataList
* /alfresco/s/owsi/permissions.csv?authority=admin Renvoi liste permissions pour une authorité

## depuis 0.8.2
* Classification gère si noeud supprimé entre temps
* Classification désactive cm:version, cm:auditable et owsi:classifiable policies 
* Swagger : modification de rendu généré
* Intégre AMP AOS
* WS /owsi/admin/configuration Renvoi configuration loggué au démarrage
* Log git.properties au démarrage.
* Amélioration du plugin contentstoreexport

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

## vers 0.9.2
* Une partie de repo-core est déplacé dans app-component. Utile uniquement pour les projets faisant des appels distants.
* ClassificationWithRooBuilder.subFolder(BussinessNode) doit utiliser doWithDestinationFolder() à la place.
* Les méthodes de AuthorityModelService et NodeSearchModelService prennent un NodeScopeBuilder en deuxième paramètre.
* IdentificationService devient IdentificationRepositoryService côté repo.

## vers 0.8.8
* Classification, remplacer `.uniqueName().moveNode()` par `moveWithUniqueName()` , et pour les parents multiples `moveFirstUniqueNameAndCreateSecondaryParents()`
* Remplacer `nodeModelRepositoryService.getUniqueChildName()` par `uniqueNameRepositoryService.getUniqueValidName()`

## vers 0.8.3
* Les fichiers toto.xml.5.2.ori doivent être renomé en toto.xml--5.2.ori.

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
