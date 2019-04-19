package fr.openwide.alfresco.repo.wsgenerator.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SwaggerResponse {
	
	int statusCode();
	String description();
	Class<?> schema() default Void.class;
}
