package fr.openwide.alfresco.demo.core.framework.spring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.acls.domain.PermissionFactory;

import fr.openwide.alfresco.app.core.framework.spring.config.AppCorePermissionConfigurerAdapter;
import fr.openwide.alfresco.app.core.framework.spring.config.EnableAppCoreSecurity;
import fr.openwide.alfresco.demo.core.application.security.service.impl.BusinessPermissionEvaluator;
import fr.openwide.core.jpa.security.model.NamedPermission;
import fr.openwide.core.jpa.security.service.NamedPermissionFactory;

@Configuration
@EnableAppCoreSecurity
public class CoreCommonSecurityConfig extends AppCorePermissionConfigurerAdapter {

	@Override
	public PermissionEvaluator applicationPermissionEvaluator() {
		return new BusinessPermissionEvaluator();
	}

	@Override
	public PermissionFactory applicationPermissionFactory() {
		return new NamedPermissionFactory(NamedPermission.class);
	}

}
