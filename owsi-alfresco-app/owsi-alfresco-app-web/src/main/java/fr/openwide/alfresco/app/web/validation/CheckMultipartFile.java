package fr.openwide.alfresco.app.web.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.hibernate.validator.constraints.Range;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckMultipartFileValidator.class)
@Documented
public @interface CheckMultipartFile {

	String message() default "{constraints.CheckMultipartFile}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String[] extension() default {};

	String[] mimetype() default {};

	Range size() default @Range(min=1);

}
