#owsi-alfresco-repo-datalistgroupe :

Ce module permet d’intégrer la fonctionnalité qui offre la possibilité au gestionnaire d’un site de créer/supprimer des groupes d’utilisateurs, associés à son site, via un modèle de datalistes : `"dlauthority:item"` 

Le module permet l’ajout et la suppression des membres : groupes ou utilisateurs.

Le nom du groupe crée sera sous la forme:

```XML
prefix + nom du groupe + suffix
```
Les prefix et suffix sont paramétrable dans le fichier : `alfresco-global.properties` :

```XML
datalistegroupe.nom_groupe.prefix=site_NOM_DU_SITE_
datalistegroupe.nom_groupe.suffix=
```

Pour intégrer la fonctionnalité proposé par le module :

  * Dans `Alfresco` :

```XML
<dependency>
	<groupId>fr.openwide.alfresco</groupId>
	<artifactId>owsi-alfresco-repo-datalistgroupe</artifactId>
	<version>0.8.1</version>
</dependency> 
```
  * Dans `Share` :
  
```XML
<dependency>
	<groupId>fr.openwide.alfresco</groupId>
	<artifactId>owsi-alfresco-share-datalistgroupe</artifactId>
	<version>0.8.1</version>
</dependency>
```