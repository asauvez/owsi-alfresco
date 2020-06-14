package fr.openwide.alfresco.repo.wsgenerator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GenerateBootstrapModel {

	String id() default "";
	
	String[] importModels() default {};
	String[] importLabels() default {};

	String[] dependsOn() default {};
}
