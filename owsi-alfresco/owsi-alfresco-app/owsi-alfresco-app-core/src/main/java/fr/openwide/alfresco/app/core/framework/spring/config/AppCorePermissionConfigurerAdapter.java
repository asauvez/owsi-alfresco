package fr.openwide.alfresco.app.core.framework.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.domain.PermissionFactory;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import fr.openwide.core.jpa.security.hierarchy.IPermissionHierarchy;
import fr.openwide.core.jpa.security.hierarchy.PermissionHierarchyImpl;

@EnableGlobalMethodSecurity(prePostEnabled = true)
public abstract class AppCorePermissionConfigurerAdapter {

	@Bean
	public abstract PermissionFactory applicationPermissionFactory();

	@Bean
	public abstract PermissionEvaluator applicationPermissionEvaluator();

	@Bean
	public IPermissionHierarchy permissionHierarchy(PermissionFactory permissionFactory) {
		PermissionHierarchyImpl hierarchy = new PermissionHierarchyImpl(permissionFactory);
		hierarchy.setHierarchy(configurePermissionHierarchy());
		return hierarchy;
	}

	protected String configurePermissionHierarchy() {
		return "";
	}

}
