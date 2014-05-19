package fr.openwide.alfresco.app.core.framework.spring.config;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.env.PropertyResolver;

import com.google.common.base.Splitter;

/**
 * <p>Ce listener Spring permet de logguer la configuration du contexte Spring lors de l'émission
 * de l'événement REFRESH.</p>
 * 
 * <p>La propriété <i>logRootApplicationContext</i>, si elle est renseignée, permet de restreindre 
 * le log à la configuration du contexte racine ou des contextes fils.</p>
 * 
 * <p>La propriété <i>propertyNamesForInfoLogLevel</i> permet de spécifier, par une liste
 * de noms de propriétés, quels sont les éléments de configuration à logguer au niveau INFO.</p>
 * 
 * <p>La propriété <i>logPattern</i> permet de spécifier le formattage des messages
 * de log émis pour chaque item de configuration. Deux arguments, le nom de la propriété
 * et sa valeur, sont passés en paramètre de String.format sur ce pattern.</p>
 */
public class ConfigurationLogger implements ApplicationListener<ContextRefreshedEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationLogger.class);

	private List<String> propertyNamesForInfoLogLevel = new ArrayList<>();
	private String logPattern = "%1$35s : %2$s";
	private Boolean logRootApplicationContext;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent refresh) {
		if (canLog(refresh)) {
			LOGGER.info("Configuration logging");
			PropertyResolver environment = BeanFactoryUtils.beanOfType(refresh.getApplicationContext(), PropertyResolver.class);
			// On logge les informations qu'on a configurées dans le contexte Spring
			for (String name : propertyNamesForInfoLogLevel) {
				logPropertyAsInfo(environment, name);
			}
			LOGGER.info("Configuration logging end");
		}
	}

	protected boolean canLog(ContextRefreshedEvent refresh) {
		if (refresh.getSource() instanceof AbstractApplicationContext) {
			AbstractApplicationContext context = (AbstractApplicationContext) refresh.getSource();
			if (context.getParent() == null) {
				return logRootApplicationContext == null || Boolean.TRUE.equals(logRootApplicationContext);
			} else {
				return logRootApplicationContext == null || Boolean.FALSE.equals(logRootApplicationContext);
			}
		}
		return false;
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

	public void setLogRootApplicationContext(Boolean logRootApplicationContext) {
		this.logRootApplicationContext = logRootApplicationContext;
	}

}
