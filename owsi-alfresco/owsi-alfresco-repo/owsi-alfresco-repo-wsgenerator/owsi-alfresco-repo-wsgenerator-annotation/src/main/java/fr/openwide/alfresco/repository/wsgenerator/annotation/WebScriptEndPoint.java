package fr.openwide.alfresco.repository.wsgenerator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import fr.openwide.alfresco.repository.wsgenerator.annotation.GenerateWebScript.WebScriptMethod;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface WebScriptEndPoint {
	
	WebScriptMethod method() default WebScriptMethod.GET;
	String url();
	
}
