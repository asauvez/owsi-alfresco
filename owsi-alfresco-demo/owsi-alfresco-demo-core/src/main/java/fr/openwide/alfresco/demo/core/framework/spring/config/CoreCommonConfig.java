package fr.openwide.alfresco.demo.core.framework.spring.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import fr.openwide.alfresco.app.core.framework.spring.config.AppCoreServiceConfig;
import fr.openwide.alfresco.app.module.framework.spring.config.AppModuleServiceConfig;
import fr.openwide.alfresco.demo.core.application.CoreApplicationPackage;
import fr.openwide.alfresco.demo.core.application.CoreCommonPackage;

@Configuration
@Import({
	CoreCommonSecurityConfig.class,		// configuration de la sécurité
	AppCoreServiceConfig.class,	// composant pour utiliser les modèles
	AppModuleServiceConfig.class		// composants utilisant le modèle owsi
})
@ComponentScan(
	basePackageClasses = {
		CoreCommonPackage.class,
		CoreApplicationPackage.class
	},
	// https://jira.springsource.org/browse/SPR-8808
	// on veut charger de manière explicite le contexte ; de ce fait,
	// on ignore l'annotation @Configuration sur le scan de package.
	excludeFilters = @Filter(Configuration.class)
)
public class CoreCommonConfig {

}
