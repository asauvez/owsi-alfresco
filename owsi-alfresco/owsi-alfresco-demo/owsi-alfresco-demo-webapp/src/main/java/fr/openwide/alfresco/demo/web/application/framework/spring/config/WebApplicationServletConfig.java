package fr.openwide.alfresco.demo.web.application.framework.spring.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import fr.openwide.alfresco.app.core.framework.spring.config.ConfigurationLoggerConfig;
import fr.openwide.alfresco.demo.web.application.WebApplicationPackage;

@Configuration
@PropertySource({
	"classpath:owsi-alfresco-app-web.properties",
	"classpath:web-application.properties"
})
@Import({
	ConfigurationLoggerConfig.class,	// Affichage de la configuration au démarrage
	WebApplicationMvcConfig.class		// Configuration Spring MVC
})
@ComponentScan(
	basePackageClasses = {
		WebApplicationPackage.class
	},
	// https://jira.springsource.org/browse/SPR-8808
	// on veut charger de manière explicite le contexte ; de ce fait,
	// on ignore l'annotation @Configuration sur le scan de package.
	excludeFilters = @Filter(Configuration.class)
)
@EnableAspectJAutoProxy // force scan for security annotations in this context
public class WebApplicationServletConfig {

}
