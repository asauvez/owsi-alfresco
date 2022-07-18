package fr.openwide.alfresco.repo.core.configurationlogger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import javax.sql.DataSource;

import org.alfresco.service.license.LicenseDescriptor;
import org.alfresco.service.license.LicenseService;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

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
public class ConfigurationLogger  
		implements ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationLogger.class);

	private ApplicationContext applicationContext;
	private Properties globalProperties;
	private StringBuilder messagesGenerated = new StringBuilder();

	@Autowired private DataSource dataSource;


	private static final String MD5_PREFIX = "MD5:";
	private MessageDigest md5MessageDigest; {
		try {
			md5MessageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}
	
	private String logPattern = "%1$45s : %2$s";

	private static List<String> propertyNamesForInfoLogLevel = new ArrayList<>();

	private void logProperties(Function<String, String> getProperty, Function<String, String> valueSubstitutor) {
		logInfo("Configuration logging");

		// RAM
		long vmMemorySize = 0;
		try {
			vmMemorySize = ((com.sun.management.OperatingSystemMXBean) ManagementFactory
			        .getOperatingSystemMXBean()).getTotalPhysicalMemorySize();
		} catch (Throwable t) {
			// ignore
		}
		logPropertyAsInfo("ram", getInMo(Runtime.getRuntime().totalMemory()) + " / " + getInMo(Runtime.getRuntime().maxMemory()) + " / " + getInMo(vmMemorySize));
		
		// Disk
		for (File disk : File.listRoots()) {
			if (disk.isDirectory()) {
				logPropertyAsInfo("disk." + disk.getAbsolutePath(), getInMo(disk.getUsableSpace()) + " / " + getInMo(disk.getTotalSpace()));
			}
		}
		
		logPropertyAsInfo("java.version", System.getProperty("java.vendor") + " / " + System.getProperty("java.version"));
		logPropertyAsInfo("os.name", System.getProperty("os.name") + " / " + System.getProperty("os.arch") + " / " + System.getProperty("os.version"));

		logCustoms();
		
		// Properties
		for (String propertyName : propertyNamesForInfoLogLevel) {
			boolean md5 = propertyName.startsWith(MD5_PREFIX);
			if (md5) {
				// Utile pour ne pas logger les passwords, juste leur signature
				propertyName = propertyName.substring(MD5_PREFIX.length());
			}
			String value = getProperty.apply(propertyName);
			if (value != null) {
				value = valueSubstitutor.apply(value);
				if (md5) {
					value = MD5_PREFIX + md5(value);
				}
				logPropertyAsInfo(propertyName, value);
			} else {
				value = "<undefined>";
			}
		}
		
		try {
			Enumeration<URL> gitPropertiesUrl = Thread.currentThread().getContextClassLoader().getResources("/git.properties");
			while (gitPropertiesUrl.hasMoreElements()) {
				URL gitPropertyUrl = gitPropertiesUrl.nextElement();
				try (InputStream in = gitPropertyUrl.openStream()) {
					Properties gitProperty = new Properties();
					gitProperty.load(in);
					
					// file:/data/(...)/alfresco/WEB-INF/lib/rome-utils-1.5.1.jar!/.gitignore
					String lib = StringUtils.substringAfterLast(StringUtils.substringBefore(gitPropertyUrl.getPath(), "!"), "/");
					
					String branch = gitProperty.getProperty("git.branch");
					String buildTime = gitProperty.getProperty("git.build.time");
					String version = gitProperty.getProperty("git.build.version");
					String msg = gitProperty.getProperty("git.commit.message.full");
					String commitId = gitProperty.getProperty("git.commit.id.abbrev");
					logPropertyAsInfo(lib, branch + ":" + version + ":" + commitId + ":" + buildTime + ":" + msg);
				}
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		
		
		logInfo("Configuration logging end");
	}
	
	private void logPropertyAsInfo(String propertyName, Object value) {
		logInfo(String.format(logPattern, propertyName, value));
	}

	private String md5(String value) {
		return bytesToHex2(md5MessageDigest.digest(value.getBytes(StandardCharsets.UTF_8))).toUpperCase();
	}
	private static String bytesToHex2(byte[] hashInBytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < hashInBytes.length; i++) {
			String hex = Integer.toHexString(0xff & hashInBytes[i]);
			if (hex.length() == 1) sb.append('0');
			sb.append(hex);
		}
		return sb.toString();
	}
	
	private String getInMo(long n) {
		return String.format("%,8d", n/1024/1024).replace((char) 0xA0, ' ') + " Mo"; 
	}
	
	public void setLogPattern(String logPattern) {
		this.logPattern = logPattern;
	}
	public void setPropertyNamesForInfoLogLevel(List<String> propertyNamesForInfoLogLevel) {
		propertyNamesForInfoLogLevel.addAll(propertyNamesForInfoLogLevel);
	}
	
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
	
	private void logCustoms() {
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
	
	private void logInfo(String msg) {
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
