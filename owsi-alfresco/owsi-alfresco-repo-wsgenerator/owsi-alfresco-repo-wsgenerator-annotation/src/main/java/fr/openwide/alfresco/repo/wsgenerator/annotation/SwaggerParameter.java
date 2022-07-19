package fr.openwide.alfresco.repo.wsgenerator.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SwaggerParameter {
	
	public enum SwaggerParameterIn { 
		PATH("path"), 
		QUERY("query"), 
		BODY("body"), 
		FORMDATA("formData")
		;
		private String label;
		private SwaggerParameterIn(String label) {
			this.label = label;
		}
		public String getLabel() {
			return label;
		}
	}
	
	String name();
	SwaggerParameterIn in() default SwaggerParameterIn.QUERY;
	String description() default "";
	boolean required() default false;
	String type() default "string";
	Class<?> schema() default Void.class;
}
