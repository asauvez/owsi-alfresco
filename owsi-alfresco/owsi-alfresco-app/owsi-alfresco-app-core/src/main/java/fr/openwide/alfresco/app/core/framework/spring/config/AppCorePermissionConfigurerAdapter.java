package fr.openwide.alfresco.app.core.framework.spring.config;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.NullRoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.acls.domain.PermissionFactory;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.core.GrantedAuthority;

import fr.openwide.core.jpa.security.hierarchy.IPermissionHierarchy;
import fr.openwide.core.jpa.security.hierarchy.PermissionHierarchyImpl;

@EnableGlobalMethodSecurity(prePostEnabled = true)
public abstract class AppCorePermissionConfigurerAdapter extends GlobalMethodSecurityConfiguration {

	public static final String LOGIN_TIME_ROLE_HIERARCHY = "loginTimeRoleHierarchy";

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
	@Primary
	public RoleHierarchy dynamicRoleHierarchy() {
		return new NullRoleHierarchy();
	}
	
	
	@Bean
	@Qualifier(LOGIN_TIME_ROLE_HIERARCHY)
	public RoleHierarchy loginTimeRoleHierarchy() {
		SecurityHierarchyBuilder builder = new SecurityHierarchyBuilder();
		addRoleHierarchy(builder);
		
		RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl() {
			@Override
			public Collection<GrantedAuthority> getReachableGrantedAuthorities(
					Collection<? extends GrantedAuthority> authorities) {
				Collection<GrantedAuthority> grantedAuthorities = super.getReachableGrantedAuthorities(authorities);
				return getLoginTimeReachableGrantedAuthorities(grantedAuthorities);
			}
		};
		roleHierarchy.setHierarchy(builder.build());
		return roleHierarchy;
	}

	protected void addRoleHierarchy(@SuppressWarnings("unused") SecurityHierarchyBuilder builder) {
	}
	
	public Collection<GrantedAuthority> getLoginTimeReachableGrantedAuthorities(Collection<GrantedAuthority> authorities) {
		return authorities;
	}
	
	
	@Override
	protected MethodSecurityExpressionHandler createExpressionHandler() {
		MethodSecurityExpressionHandler expressionHandler = super.createExpressionHandler();
		((DefaultMethodSecurityExpressionHandler) expressionHandler).setRoleHierarchy(dynamicRoleHierarchy());
		return expressionHandler;
	}
}
