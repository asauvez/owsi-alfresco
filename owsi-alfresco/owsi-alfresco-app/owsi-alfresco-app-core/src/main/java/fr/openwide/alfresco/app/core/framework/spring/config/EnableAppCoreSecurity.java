package fr.openwide.alfresco.app.core.framework.spring.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import fr.openwide.alfresco.app.core.security.model.PrincipalType;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(PrincipalTypeImportSelector.class)
public @interface EnableAppCoreSecurity {

	PrincipalType value() default PrincipalType.NAMED_USER;

}
