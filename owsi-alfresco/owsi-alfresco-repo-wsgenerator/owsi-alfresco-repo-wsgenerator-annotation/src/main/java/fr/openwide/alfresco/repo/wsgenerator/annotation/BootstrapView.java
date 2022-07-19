package fr.openwide.alfresco.repo.wsgenerator.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface BootstrapView {

	String id() default "";
	String description() default "";
	String checkPath(); // "/${spaces.company_home.childname}/st:sites/cm:acme/cm:dataLists/cm:myDataList"
	String path(); // "/${spaces.company_home.childname}/st:sites/cm:acme"
	String location(); // "alfresco/module/acme-ged-platform/view/tableDomaine.model.xml"
	
	public enum BootstrapViewUuidBinding {
		CREATE_NEW, CREATE_NEW_WITH_UUID, REMOVE_EXISTING, REPLACE_EXISTING, UPDATE_EXISTING, THROW_ON_COLLISION
	}
	BootstrapViewUuidBinding uuidBinding() default BootstrapViewUuidBinding.UPDATE_EXISTING;
	
	String[] dependsOn() default {};
}
