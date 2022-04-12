package fr.openwide.alfresco.repo.core.configurationlogger;

import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.alfresco.service.license.LicenseDescriptor;
import org.alfresco.service.license.LicenseService;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import fr.openwide.alfresco.api.core.log.AbstractConfigurationLogger;
import fr.openwide.alfresco.repo.contentstoreexport.service.impl.ContentStoreExportServiceImpl;

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
public class ConfigurationLogger extends AbstractConfigurationLogger 
		implements ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationLogger.class);

	private ApplicationContext applicationContext;
	private Properties globalProperties;
	private StringBuilder messagesGenerated = new StringBuilder();

	@Autowired private DataSource dataSource;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (event.getApplicationContext() != applicationContext || globalProperties == null) {
			return;
		}
		
		Properties propertiesAvecSystem = new Properties(globalProperties);
		propertiesAvecSystem.putAll(System.getProperties());
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		StrSubstitutor strSubstitutor = new StrSubstitutor((Map) propertiesAvecSystem);
		
		logProperties(propertiesAvecSystem::getProperty, strSubstitutor::replace);
	}
	
	@Override
	protected void logCustoms() {
		// Plus vrai en 7.2
		if (dataSource instanceof BasicDataSource) {
			BasicDataSource basicDataSource = (BasicDataSource) dataSource;
			logPropertyAsInfo("db.maximumPoolSize", basicDataSource.getMaxIdle() + " / " + basicDataSource.getMaxActive());
		}
		
		LicenseService licenseService = applicationContext.getBean(LicenseService.class);
		LicenseDescriptor license = licenseService.getLicense();
		logPropertyAsInfo("alfresco.licenseValid", licenseService.isLicenseValid());
		if (license != null) {
			logPropertyAsInfo("alfresco.licenseHolder", license.getHolderOrganisation());
			logPropertyAsInfo("alfresco.users", (license.getMaxUsers() != null) ? license.getMaxUsers() : "Unlimited");
			logPropertyAsInfo("alfresco.licenseValidUntil", license.getValidUntil());
			logPropertyAsInfo("alfresco.licenseValidFor.days", license.getRemainingDays());
		}
	}

	public String getMessagesGenerated() {
		return messagesGenerated.toString();
	}
	
	@Override
	protected void logInfo(String msg) {
		LOGGER.info(msg);
		messagesGenerated.append(msg).append("\n");
		ContentStoreExportServiceImpl.configurationLogger.append(msg).append("\n");
	}

	public void setGlobalProperties(Properties globalProperties) {
		this.globalProperties = globalProperties;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

}
