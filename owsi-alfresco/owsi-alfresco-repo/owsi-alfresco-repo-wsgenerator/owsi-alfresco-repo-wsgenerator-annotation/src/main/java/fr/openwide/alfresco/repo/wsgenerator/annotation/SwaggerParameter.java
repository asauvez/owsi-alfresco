package fr.openwide.alfresco.repo.wsgenerator.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SwaggerParameter {
	
	public enum SwaggerParameterIn { PATH, QUERY, BODY }
	
	String name();
	SwaggerParameterIn in() default SwaggerParameterIn.QUERY;
	String description() default "";
	boolean required() default false;
	String type() default "string";
	Class<?> schema() default Void.class;
}
