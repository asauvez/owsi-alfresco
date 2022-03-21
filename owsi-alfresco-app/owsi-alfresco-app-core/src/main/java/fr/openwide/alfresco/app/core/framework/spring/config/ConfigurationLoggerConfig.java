package fr.openwide.alfresco.app.core.framework.spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import fr.openwide.alfresco.app.core.framework.spring.env.ConfigurationLogger;

@Configuration
public class ConfigurationLoggerConfig {

	@Autowired
	private Environment environment;

	@Bean
	public ConfigurationLogger configurationLogger() {
		ConfigurationLogger configurationLogger = new ConfigurationLogger();
		configurationLogger.setPropertyNamesForInfoLogLevel(environment.getRequiredProperty("application.propertyNamesForInfoLogLevel"));
		return configurationLogger;
	}

}
