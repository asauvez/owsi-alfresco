package fr.openwide.alfresco.app.core.framework.spring.env;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.DatatypeConverter;

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

	private static final String MD5_PREFIX = "MD5:";
	private MessageDigest md5MessageDigest; {
		try {
			md5MessageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}

	private List<String> propertyNamesForInfoLogLevel = new ArrayList<>();
	private String logPattern = "%1$35s : %2$s";
	private ApplicationContext applicationContext;

	private LicenseRestrictions lastLicenseRestrictions;
	private long lastAlfrescoConnectionConnection = 0;
	private String lastAlfrescoConnectionError = null;
	private Optional<String> repositoryWarning = Optional.empty();
	private Optional<String> repositoryError = Optional.empty();;
	
	@Autowired private Environment environment;
	@Autowired (required=false) private HikariDataSource dataSource;
	
	@Autowired private LicenseService licenceService;
	@Autowired private RunAsUserManager runAsUserManager;

	@SuppressWarnings("restriction")
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (event.getApplicationContext() != applicationContext) {
			return;
		}

		refreshLicenseRestrictions();
		
		LOGGER.info("Configuration logging");
		
		long vmMemorySize = 0;
		try {
			vmMemorySize = ((com.sun.management.OperatingSystemMXBean) ManagementFactory
			        .getOperatingSystemMXBean()).getTotalPhysicalMemorySize();
		} catch (Throwable t) {
			// ignore
		}
		logPropertyAsInfo("ram", getInMo(Runtime.getRuntime().totalMemory()) + " / " + getInMo(Runtime.getRuntime().maxMemory()) + "/" + getInMo(vmMemorySize));
		for (File disk : File.listRoots()) {
			if (disk.isDirectory()) {
				logPropertyAsInfo("disk." + disk.getAbsolutePath(), getInMo(disk.getUsableSpace()) + " / " + getInMo(disk.getTotalSpace()));
			}
		}
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

		PropertyResolver resolver = BeanFactoryUtils.beanOfType(applicationContext, PropertyResolver.class);
		// Logging configured properties
		for (String propertyName : propertyNamesForInfoLogLevel) {
			boolean md5 = propertyName.startsWith(MD5_PREFIX);
			if (md5) {
				// Utile pour ne pas logger les passwords, juste leur signature
				propertyName = propertyName.substring(MD5_PREFIX.length());
			}

			String value = resolver.getRequiredProperty(propertyName);
			if (md5) {
				value = MD5_PREFIX + md5(value);
			}
			logPropertyAsInfo(propertyName, value);
		}
		LOGGER.info("Configuration logging end");
	}

	private String md5(String value) {
		try {
			return DatatypeConverter.printHexBinary(md5MessageDigest.digest(value.getBytes("UTF-8"))).toUpperCase();
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
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

}
