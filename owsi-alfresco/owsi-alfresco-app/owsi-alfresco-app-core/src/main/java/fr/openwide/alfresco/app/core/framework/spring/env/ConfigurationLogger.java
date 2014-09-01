package fr.openwide.alfresco.app.core.framework.spring.env;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.PropertyResolver;

import com.google.common.base.Splitter;

/**
 * <p>Ce listener Spring permet de logguer la configuration du contexte Spring lors de l'émission
 * de l'événement REFRESH.</p>
 * 
 * <p>La propriété <i>propertyNamesForInfoLogLevel</i> permet de spécifier, par une liste
 * de noms de propriétés, quels sont les éléments de configuration à logguer au niveau INFO.</p>
 * 
 * <p>La propriété <i>logPattern</i> permet de spécifier le formattage des messages
 * de log émis pour chaque item de configuration. Deux arguments, le nom de la propriété
 * et sa valeur, sont passés en paramètre de String.format sur ce pattern.</p>
 */
public class ConfigurationLogger implements ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationLogger.class);

	private List<String> propertyNamesForInfoLogLevel = new ArrayList<>();
	private String logPattern = "%1$35s : %2$s";
	private ApplicationContext applicationContext;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (event.getApplicationContext() != applicationContext) {
			return;
		}
		LOGGER.info("Configuration logging");
		PropertyResolver resolver = BeanFactoryUtils.beanOfType(applicationContext, PropertyResolver.class);
		// Logging configured properties
		for (String name : propertyNamesForInfoLogLevel) {
			logPropertyAsInfo(resolver, name);
		}
		LOGGER.info("Configuration logging end");
	}

	protected void logPropertyAsInfo(PropertyResolver resolver, String propertyName) {
		logPropertyAsInfo(propertyName, resolver.getRequiredProperty(propertyName));
	}

	protected void logPropertyAsInfo(String propertyName, String value) {
		LOGGER.info(String.format(logPattern, propertyName, value));
	}

	public void setPropertyNamesForInfoLogLevel(String names) {
		propertyNamesForInfoLogLevel.addAll(Splitter.on(',').splitToList(names));
	}

	public void setLogPattern(String logPattern) {
		this.logPattern = logPattern;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

}
