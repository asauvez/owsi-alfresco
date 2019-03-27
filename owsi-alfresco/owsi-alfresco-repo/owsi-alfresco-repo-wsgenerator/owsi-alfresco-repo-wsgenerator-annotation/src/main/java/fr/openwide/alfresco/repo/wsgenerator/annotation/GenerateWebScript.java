package fr.openwide.alfresco.repo.wsgenerator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import fr.openwide.alfresco.repo.wsgenerator.model.WebScriptParam;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface GenerateWebScript {
	
	@SuppressWarnings("rawtypes")
	Class<? extends WebScriptParam> paramClass() default WebScriptParam.class; 
	
	enum WebScriptMethod { GET, PUT, POST, DELETE, HEAD, OPTIONS; }
	WebScriptMethod method() default WebScriptMethod.GET;

	String[] url() default {};
	
	String shortName() default "";
	String description() default "";
	
	String wsFolder() default "";
	String wsName() default "";
	
	String family() default "";
	
	enum GenerateWebScriptFormat { ARGUMENT, EXTENSION, ANY }
	GenerateWebScriptFormat format() default GenerateWebScriptFormat.ANY;
	String formatDefault() default "text";
	
	enum GenerateWebScriptAuthentication { NONE, GUEST, USER, ADMIN }
	GenerateWebScriptAuthentication authentication() default GenerateWebScriptAuthentication.USER;
	
	enum GenerateWebScriptTransaction { NONE, REQUIRED, REQUIRESNEW }
	GenerateWebScriptTransaction transaction() default GenerateWebScriptTransaction.REQUIRED;

	enum GenerateWebScriptTransactionAllow { READONLY, READWRITE }
	GenerateWebScriptTransactionAllow transactionAllow() default GenerateWebScriptTransactionAllow.READWRITE;
	
	String beanParent() default "webscript";
	
	/** Mettre à true s'il le WS implémente DeclarativeWebScript */
	boolean useViewFile() default false;
}
