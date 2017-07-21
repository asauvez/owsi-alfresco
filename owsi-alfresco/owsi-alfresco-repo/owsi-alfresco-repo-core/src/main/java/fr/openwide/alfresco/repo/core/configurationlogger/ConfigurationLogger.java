package fr.openwide.alfresco.repo.core.configurationlogger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

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

	private static List<String> propertyNamesForInfoLogLevel = new ArrayList<>();
	
	private String logPattern = "%1$45s : %2$s";
	private ApplicationContext applicationContext;
	private Properties globalProperties;

	@Autowired
	private BasicDataSource dataSource;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (event.getApplicationContext() != applicationContext || globalProperties == null) {
			return;
		}
		LOGGER.info("Configuration logging");
		
		logPropertyAsInfo("ram", getInMo(Runtime.getRuntime().totalMemory()) + " / " + getInMo(Runtime.getRuntime().maxMemory()));
		for (File disk : File.listRoots()) {
			if (disk.isDirectory()) {
				logPropertyAsInfo("disk." + disk.getAbsolutePath(), getInMo(disk.getUsableSpace()) + " / " + getInMo(disk.getTotalSpace()));
			}
		}
		
		if (dataSource != null) {
			logPropertyAsInfo("db.maximumPoolSize", dataSource.getMaxIdle() + "/" + dataSource.getMaxActive());
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		StrSubstitutor strSubstitutor = new StrSubstitutor((Map) globalProperties);
		for (String propertyName : propertyNamesForInfoLogLevel) {
			String value = globalProperties.getProperty(propertyName);
			if (value != null) {
				value = strSubstitutor.replace(value);
				logPropertyAsInfo(propertyName, value);
			} else {
				throw new IllegalStateException("Property not found " + propertyName);
			}
		}
		LOGGER.info("Configuration logging end");
	}

	protected void logPropertyAsInfo(String propertyName, String value) {
		LOGGER.info(String.format(logPattern, propertyName, value));
	}

	private static String getInMo(long n) {
		return String.format("%,8d", n/1024/1024) + " Mo"; 
	}
	
	public void setPropertyNamesForInfoLogLevel(List<String> propertyNamesForInfoLogLevel) {
		ConfigurationLogger.propertyNamesForInfoLogLevel.addAll(propertyNamesForInfoLogLevel);
	}

	public void setLogPattern(String logPattern) {
		this.logPattern = logPattern;
	}
	
	public void setGlobalProperties(Properties globalProperties) {
		this.globalProperties = globalProperties;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

}
