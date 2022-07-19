package fr.openwide.alfresco.repo.wsgenerator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * La classe doit hériter de AbstractPatch.
 * 
 * Il faut probablement mettre un dependsOn si on utilise un model custom.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GeneratePatch {
	
	String id() default "";

	String[] dependsOn() default { "dictionaryBootstrap" };
}
