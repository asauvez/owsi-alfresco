package fr.openwide.alfresco.app.core.framework.spring.config;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import fr.openwide.alfresco.app.core.security.model.PrincipalType;

/**
 * Select the config to import depending the PrincipalType choosed.
 * 
 * {@see org.springframework.context.annotation.AdviceModeImportSelector}
 * {@see org.springframework.context.annotation.AnnotationConfigUtils#attributesFor}
 */
public class PrincipalTypeImportSelector implements ImportSelector {

	private static Class<?> ANNOTATION = EnableAppCoreSecurity.class;
	private static String ENUM_NAME = "value";

	@Override
	public final String[] selectImports(AnnotationMetadata importingClassMetadata) {
		AnnotationAttributes attributes =  AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(ANNOTATION.getName(), false));
		Assert.notNull(attributes, String.format("@%s is not present on importing class '%s' as expected",
				ANNOTATION.getSimpleName(), importingClassMetadata.getClassName()));
		PrincipalType enumeration = attributes.getEnum(ENUM_NAME);
		String[] imports = selectImports(enumeration);
		Assert.notNull(imports, String.format("Unknown enum: '%s'", enumeration));
		return imports;
	}

	protected String[] selectImports(PrincipalType type) {
		switch (type) {
			case USER_DETAILS:
				return new String[] { UserDetailsAppCoreSecurityConfig.class.getName() };
			case NAMED_USER:
				return new String[] { NamedUserAppCoreSecurityConfig.class.getName() };
			default:
				return null;
		}
	}

}
