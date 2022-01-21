package fr.openwide.alfresco.repo.core.configurationlogger;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateService;

@GenerateService(id="owsi.alfrescoGlobalProperties")
public class AlfrescoGlobalProperties implements InitializingBean {

	@Autowired @Qualifier("global-properties")
	private Properties globalProperties;

	@Override
	public void afterPropertiesSet() throws Exception {
		globalProperties = new Properties(globalProperties);
		
		// Rajoute les system properties, venant par exemple de docker-compose.
		globalProperties.putAll(System.getProperties());
	}

	public Optional<String> getPropertyOptional(String key) {
		String value = globalProperties.getProperty(key);
		return (StringUtils.isNotEmpty(value)) ? Optional.of(value) : Optional.empty();
	}
	public String getPropertyMandatory(String key) {
		String value = globalProperties.getProperty(key);
		if (value == null) {
			throw new IllegalStateException("Property '" + key + "' is missing.");
		}
		return value;
	}
	public String getProperty(String key, String defaultValue) {
		return globalProperties.getProperty(key, defaultValue);
	}
	
	public int getPropertyInt(String key) {
		return Integer.parseInt(getPropertyMandatory(key));
	}
	public int getPropertyInt(String key, int defaultValue) {
		return Integer.parseInt(getProperty(key, Integer.toString(defaultValue)));
	}

	public long getPropertyLong(String key) {
		return Long.parseLong(getPropertyMandatory(key));
	}
	public long getPropertyLong(String key, long defaultValue) {
		return Long.parseLong(getProperty(key, Long.toString(defaultValue)));
	}

	public boolean getPropertyBoolean(String key) {
		return Boolean.parseBoolean(getPropertyMandatory(key));
	}
	public boolean getPropertyBoolean(String key, boolean defaultValue) {
		return Boolean.parseBoolean(getProperty(key, Boolean.toString(defaultValue)));
	}
	public List<String> getPropertyList(String key) {
		return Arrays.asList(getPropertyMandatory(key).split(","));
	}
	public List<String> getPropertyList(String key, String defaultValue) {
		return Arrays.asList(getProperty(key, defaultValue).split(","));
	}

}
