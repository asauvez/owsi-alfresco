package fr.openwide.alfresco.app.core.framework.spring.env;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertyResolver;

import com.google.common.base.Splitter;
import com.zaxxer.hikari.HikariDataSource;

import fr.openwide.alfresco.app.core.licence.model.LicenseRestrictions;
import fr.openwide.alfresco.app.core.licence.service.LicenseService;
import fr.openwide.alfresco.app.core.security.service.RunAsUserManager;

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
	
	private Optional<String> repositoryWarning = Optional.empty();
	private Optional<String> repositoryError = Optional.empty();;
	
	@Autowired private Environment environment;
	@Autowired (required=false) private HikariDataSource dataSource;
	
	@Autowired private LicenseService licenceService;
	@Autowired private RunAsUserManager runAsUserManager;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (event.getApplicationContext() != applicationContext) {
			return;
		}
		
		LicenseRestrictions restrictions = null;
		String alfrescoConnectionError = null;
		try {
			restrictions = runAsUserManager.runAsSystem(() -> {
				return licenceService.getRestrictions();
			});
			if (restrictions.isReadOnly()) {
				String errorReadOnlyMessage = environment.getRequiredProperty("application.repository.errorReadOnly.message");
				repositoryError = Optional.of(errorReadOnlyMessage);
				LOGGER.error(repositoryError.get());
			}
			int warningLicenseValidForDays = environment.getRequiredProperty("application.repository.warningLicenseValidFor.days", Integer.class);
			if (restrictions.getLicenseValidForDays() != null && restrictions.getLicenseValidForDays() < warningLicenseValidForDays) {
				String warningLicenseValidForMessage = environment.getRequiredProperty("application.repository.warningLicenseValidFor.message");
				repositoryWarning = Optional.of(MessageFormat.format(warningLicenseValidForMessage,
						restrictions.getLicenseValidForDays(),
						restrictions.getLicenseValidUntil(),
						warningLicenseValidForDays));
				LOGGER.warn(repositoryWarning.get());
			}
		} catch (Exception e) {
			alfrescoConnectionError = e.toString();
		}
		
		LOGGER.info("Configuration logging");
		
		logPropertyAsInfo("ram", getInMo(Runtime.getRuntime().totalMemory()) + " / " + getInMo(Runtime.getRuntime().maxMemory()));
		for (File disk : File.listRoots()) {
			if (disk.isDirectory()) {
				logPropertyAsInfo("disk." + disk.getAbsolutePath(), getInMo(disk.getUsableSpace()) + " / " + getInMo(disk.getTotalSpace()));
			}
		}
		if (dataSource != null) {
			logPropertyAsInfo("db.maximumPoolSize", dataSource.getMaximumPoolSize());
		}
		
		if (restrictions != null) {
			logPropertyAsInfo("alfresco.readOnly", restrictions.isReadOnly());
			logPropertyAsInfo("alfresco.licenseHolder", restrictions.getLicenseHolder());
			logPropertyAsInfo("alfresco.users", (restrictions.getUsers() != null) ? restrictions.getUsers() : "Unlimited");
			logPropertyAsInfo("alfresco.licenseValidUntil", restrictions.getLicenseValidUntil());
			logPropertyAsInfo("alfresco.licenseValidFor.days", restrictions.getLicenseValidForDays());
		} else {
			logPropertyAsInfo("alfresco.error", alfrescoConnectionError);
		}

		PropertyResolver resolver = BeanFactoryUtils.beanOfType(applicationContext, PropertyResolver.class);
		// Logging configured properties
		for (String propertyName : propertyNamesForInfoLogLevel) {
			logPropertyAsInfo(propertyName, resolver.getRequiredProperty(propertyName));
		}
		LOGGER.info("Configuration logging end");
	}

	private void logPropertyAsInfo(String propertyName, Object value) {
		LOGGER.info(String.format(logPattern, propertyName, value));
	}

	private static String getInMo(long n) {
		return String.format("%,8d", n/1024/1024) + " Mo"; 
	}
	
	public void setPropertyNamesForInfoLogLevel(String names) {
		propertyNamesForInfoLogLevel.addAll(Splitter.on(',').splitToList(names));
	}

	public void setLogPattern(String logPattern) {
		this.logPattern = logPattern;
	}

	public Optional<String> getRepositoryWarning() {
		return repositoryWarning;
	}
	public Optional<String> getRepositoryError() {
		return repositoryError;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

}
