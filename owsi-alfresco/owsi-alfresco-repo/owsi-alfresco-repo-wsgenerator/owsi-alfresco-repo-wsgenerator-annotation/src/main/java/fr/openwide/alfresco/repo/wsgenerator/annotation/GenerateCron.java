package fr.openwide.alfresco.repo.wsgenerator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GenerateCron {
	
	String id() default "";

	String[] dependsOn() default {};

	String cronExpression();
	String startDelay() default "0";
	
	String enable() default "true";
	boolean readOnly() default false;
	String runAs() default "System";
	boolean logAsInfo() default false;
}
