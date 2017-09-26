package fr.openwide.alfresco.api.core.log;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.xml.bind.DatatypeConverter;

public abstract  class AbstractConfigurationLogger {

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

	protected abstract void logInfo(String msg);
	protected abstract void logCustoms();
	
	@SuppressWarnings("restriction")
	protected void logProperties(Function<String, String> getProperty, Function<String, String> valueSubstitutor) {
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
				throw new IllegalStateException("Property not found " + propertyName);
			}
		}
		logInfo("Configuration logging end");
	}
	
	protected void logPropertyAsInfo(String propertyName, Object value) {
		logInfo(String.format(logPattern, propertyName, value));
	}

	private String md5(String value) {
		try {
			return DatatypeConverter.printHexBinary(md5MessageDigest.digest(value.getBytes("UTF-8"))).toUpperCase();
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}
	
	protected String getInMo(long n) {
		return String.format("%,8d", n/1024/1024).replace((char) 0xA0, ' ') + " Mo"; 
	}
	
	public void setLogPattern(String logPattern) {
		this.logPattern = logPattern;
	}
	public void setPropertyNamesForInfoLogLevel(List<String> propertyNamesForInfoLogLevel) {
		AbstractConfigurationLogger.propertyNamesForInfoLogLevel.addAll(propertyNamesForInfoLogLevel);
	}
}
