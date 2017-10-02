package fr.openwide.alfresco.app.core.framework.spring.env;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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

import fr.openwide.alfresco.api.core.log.AbstractConfigurationLogger;
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
public class ConfigurationLogger extends AbstractConfigurationLogger
		implements ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationLogger.class);

	private ApplicationContext applicationContext;

	private LicenseRestrictions lastLicenseRestrictions;
	private long lastAlfrescoConnectionConnection = 0;
	private String lastAlfrescoConnectionError = "No initial license request";
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
		if (environment.getProperty("application.repository.licenseCheck.onStartup", Boolean.class, Boolean.TRUE)) {
			refreshLicenseRestrictions();
		}
		
		PropertyResolver resolver = BeanFactoryUtils.beanOfType(applicationContext, PropertyResolver.class);
		
		logProperties(resolver::getRequiredProperty, s -> s);
	}

	@Override
	protected void logCustoms() {
		if (dataSource != null) {
			logPropertyAsInfo("db.maximumPoolSize", dataSource.getMaximumPoolSize());
		}
		
		if (lastLicenseRestrictions != null) {
			logPropertyAsInfo("alfresco.readOnly", lastLicenseRestrictions.isReadOnly());
			logPropertyAsInfo("alfresco.licenseHolder", lastLicenseRestrictions.getLicenseHolder());
			logPropertyAsInfo("alfresco.users", (lastLicenseRestrictions.getUsers() != null) ? lastLicenseRestrictions.getUsers() : "Unlimited");
			logPropertyAsInfo("alfresco.licenseValidUntil", lastLicenseRestrictions.getLicenseValidUntil());
			logPropertyAsInfo("alfresco.licenseValidFor.days", lastLicenseRestrictions.getLicenseValidForDays());
		} else {
			logPropertyAsInfo("alfresco.error", lastAlfrescoConnectionError);
		}
	}
	
	@Override
	protected void logInfo(String msg) {
		LOGGER.info(msg);
	}
	
	private void refreshLicenseRestrictions() {
		int licenseCheckHours = environment.getRequiredProperty("application.repository.licenseCheck.hours", Integer.class);
		if (System.currentTimeMillis() - lastAlfrescoConnectionConnection >= TimeUnit.MILLISECONDS.convert(licenseCheckHours, TimeUnit.HOURS)) {
			try {
				lastLicenseRestrictions = runAsUserManager.runAsSystem(() -> {
					return licenceService.getRestrictions();
				});
				repositoryError = Optional.empty();
				if (lastLicenseRestrictions.isReadOnly()) {
					String errorReadOnlyMessage = environment.getRequiredProperty("application.repository.errorReadOnly.message");
					repositoryError = Optional.of(errorReadOnlyMessage);
					LOGGER.error(repositoryError.get());
				}
				int warningLicenseValidForDays = environment.getRequiredProperty("application.repository.warningLicenseValidFor.days", Integer.class);
				if (lastLicenseRestrictions.getLicenseValidForDays() != null && lastLicenseRestrictions.getLicenseValidForDays() < warningLicenseValidForDays) {
					String warningLicenseValidForMessage = environment.getRequiredProperty("application.repository.warningLicenseValidFor.message");
					repositoryWarning = Optional.of(MessageFormat.format(warningLicenseValidForMessage,
							lastLicenseRestrictions.getLicenseValidForDays(),
							lastLicenseRestrictions.getLicenseValidUntil(),
							warningLicenseValidForDays));
					LOGGER.warn(repositoryWarning.get());
				}
				lastAlfrescoConnectionConnection = System.currentTimeMillis();
			} catch (Exception e) {
				lastAlfrescoConnectionError = e.toString();
				repositoryError = Optional.of(lastAlfrescoConnectionError);
			}
		}
	}
	public Optional<String> getRepositoryWarning() {
		refreshLicenseRestrictions();
		return repositoryWarning;
	}
	public Optional<String> getRepositoryError() {
		refreshLicenseRestrictions();
		return repositoryError;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	public void setPropertyNamesForInfoLogLevel(String names) {
		setPropertyNamesForInfoLogLevel(Splitter.on(',').splitToList(names));
	}

}
