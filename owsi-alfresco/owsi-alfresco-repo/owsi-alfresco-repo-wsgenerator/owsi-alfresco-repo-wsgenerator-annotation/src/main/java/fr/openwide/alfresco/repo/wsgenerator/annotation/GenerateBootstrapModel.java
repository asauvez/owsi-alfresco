package fr.openwide.alfresco.repo.wsgenerator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Exemple :
 * 
 * @GenerateBootstrapModel(
 *	importModels = "alfresco/module/xxx-ged-platform/xxx-model.xml",
 *  generateJava = true,
 *	importViews = {
 *		@BootstrapView(
 *			checkPath="/${spaces.company_home.childname}/st:sites/cm:acme/cm:dataLists/cm:myDatalist",
 *			path="/${spaces.company_home.childname}/st:sites/cm:acme",
 *			location="alfresco/module/xxx-ged-platform/view/tableDomaine.model.xml", 
 *			dependsOn="patch.fr.smile.myPatch"
 *		)
 *	}
 *	dependsOn = "owsi.dictionaryBootstrap"
 * )
 * 
 * Pour l'import des vues :
 * https://docs.alfresco.com/content-services/latest/develop/repo-ext-points/bootstrap-content/
 */


@Target({ElementType.TYPE, ElementType.PACKAGE})
@Retention(RetentionPolicy.RUNTIME)
public @interface GenerateBootstrapModel {

	String id() default "";
	
	String[] importModels() default {};
	boolean generateJava() default false;
	
	String[] importLabels() default {};
	
	BootstrapView[] importViews() default {};

	String[] dependsOn() default {};
}
