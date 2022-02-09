package fr.openwide.alfresco.repo.wsgenerator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Exemple :
 * 
 * @GenerateJavaModel(
 *	importModels = "alfresco/module/xxx-ged-platform/xxx-model.xml",
 * )
 */


@Target({ElementType.TYPE, ElementType.PACKAGE})
@Retention(RetentionPolicy.RUNTIME)
public @interface GenerateJavaModel {

	String[] importModels() default {};
	
	boolean useJackson() default false; 
	boolean useBean() default true;
}