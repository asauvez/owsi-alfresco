package fr.openwide.alfresco.query.web.search.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;

import fr.openwide.alfresco.query.web.search.service.impl.NodeFormQueryServiceImpl;

@Configuration
@ComponentScan(
	basePackageClasses = {
		NodeFormQueryServiceImpl.class
	},
	// https://jira.springsource.org/browse/SPR-8808
	// on veut charger de manière explicite le contexte ; de ce fait,
	// on ignore l'annotation @Configuration sur le scan de package.
	excludeFilters = @Filter(Configuration.class)
)
public class AlfrescoQueryWebConfig {

}
