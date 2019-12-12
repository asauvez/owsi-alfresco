Tree Aspect
===========

## RegisterRootPropertyName
 * Permet de copier une propriété dans une autre, si un aspect précit est présent.

   * registerCopyPropertyName(QName aspectOfRootNode, QName propertyWhereCopy) 
     * copy le cm:name dans "propertyWhereCopy", quand un node a l'aspect "aspectOfRootNode"
   
   * registerCopyPropertyName(QName aspectOfRootNode, QName propertyToCopy, QName propertyWhereCopy) 
     * copy la propiété "propertyToCopy" dans "propertyWhereCopy", quand un node a l'aspect "aspectOfRootNode"

## TreeAspectService
 * Permet de copier toutes les propriétés ainsi que les aspects dans les enfants d'un node.

   * registerAspect(QName aspect)
     * copi toute les properties de l'aspect avec le QName mis en paramètre
   * registerAspect(QName aspect, boolean breakInheritanceDuringMove)
     * avec breakInheritanceDuringMove à false, permettera de ne pas supprimer les aspects quand on les bouge en dehors d'un dossier avec l'aspect
     
     
## Exemple d'utlisation

On a un marché "toto" avec un numéro de marché 1234.

Pour ce marché on a plusieurs documents associés. On veut que tous les documents aient les métadatas du marché pour que l'on puisse les rechercher facilement.
On veut donc copier certaines les propriétés du marché parent.
Comme le nom du marché est une propriété on combine RegisterRootPropertyName & TreeAspectService :
 * On définit le marché parent qui sera le seul à avoir l'aspect `marche:marche` permettant la copie du nom dans la proprité :
    * registerCopyPropertyName("marche:marche", "marche:marcheName")
    * registerAspect("marche:marcheMeta")
 * On a donc lors de la création d'un marché l'ajout de l'aspect `marche:marcheMeta`, ce qui permet de propager  les propriétés aux enfants
 
Toto (cm:name:toto, marche:marcheNuméro:1234, marche:marcheName:toto)           /aspects : "marche:marche", "marche:marcheMeta"
| Doc1 (cm:name:doc1, marche:marcheNuméro:1234, marche:marcheName:toto)         /aspects : "marche:marche", "marche:marcheMeta"
| Doc2 (cm:name:doc2, marche:marcheNuméro:1234, marche:marcheName:toto)         /aspects : "marche:marche", "marche:marcheMeta"
| Dossier1 (cm:name:Dossier1, marche:marcheNuméro:1234, marche:marcheName:toto) /aspects : "marche:marche", "marche:marcheMeta"
| | Doc3 (cm:name:doc3, marche:marcheNuméro:1234, marche:marcheName:toto)       /aspects : "marche:marche", "marche:marcheMeta"
 
 
 
 
 ## ANNEXE
 
 * Model
 `<aspect name="marche:marche">
    <mandatory-aspects>
        <aspect>marche:marcheMeta</aspect>
    </mandatory-aspects>
 </aspect>
 <aspect name="marche:marcheMeta">
    <properties>
        <property name="marche:marcheNuméro">
            <type>d:text</type>
        </property>
        <property name="marche:marcheName">
            <type>d:text</type>
        </property>
    </properties>
 </aspect>` 
