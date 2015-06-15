package fr.openwide.alfresco.app.core.framework.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.acls.domain.PermissionFactory;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

import fr.openwide.core.jpa.security.hierarchy.IPermissionHierarchy;
import fr.openwide.core.jpa.security.hierarchy.PermissionHierarchyImpl;

@EnableGlobalMethodSecurity(prePostEnabled = true)
public abstract class AppCorePermissionConfigurerAdapter extends GlobalMethodSecurityConfiguration {

	@Bean
	public abstract PermissionFactory applicationPermissionFactory();

	@Bean
	public abstract PermissionEvaluator applicationPermissionEvaluator();

	@Bean
	public IPermissionHierarchy permissionHierarchy(PermissionFactory permissionFactory) {
		SecurityHierarchyBuilder builder = new SecurityHierarchyBuilder();
		addPermissionHierarchy(builder);
		
		PermissionHierarchyImpl hierarchy = new PermissionHierarchyImpl(permissionFactory);
		hierarchy.setHierarchy(builder.build());
		return hierarchy;
	}

	protected void addPermissionHierarchy(@SuppressWarnings("unused") SecurityHierarchyBuilder builder) {
	}

	
	@Bean
	public RoleHierarchy roleHierarchy() {
		SecurityHierarchyBuilder builder = new SecurityHierarchyBuilder();
		addRoleHierarchy(builder);
		
		RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
		roleHierarchy.setHierarchy(builder.build());
		return roleHierarchy;
	}

	protected void addRoleHierarchy(@SuppressWarnings("unused") SecurityHierarchyBuilder builder) {
	}
	
	@Override
	protected MethodSecurityExpressionHandler createExpressionHandler() {
		MethodSecurityExpressionHandler expressionHandler = super.createExpressionHandler();
		((DefaultMethodSecurityExpressionHandler) expressionHandler).setRoleHierarchy(roleHierarchy());
		return expressionHandler;
	}
}
