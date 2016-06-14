package fr.openwide.alfresco.repository.core.resetModuleVersion.service.impl;

import java.io.Serializable;
import java.util.Properties;

import org.alfresco.repo.admin.registry.RegistryKey;
import org.alfresco.repo.admin.registry.RegistryService;
import org.alfresco.repo.module.ModuleComponentHelper;
import org.alfresco.repo.module.ModuleVersionNumber;
import org.alfresco.repo.module.tool.ModuleManagementToolException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.util.VersionNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class ResetModuleVersionServiceImpl implements InitializingBean {

	private String modules = "";
	private Properties globalProperties;
	private RegistryService registryService;

	/** @See ModuleComponentHelper */
	private static final String REGISTRY_PROPERTY_CURRENT_VERSION = "currentVersion";
	private static final String REGISTRY_PATH_MODULES = "modules";
	
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Override
	public void afterPropertiesSet() throws Exception {
		if (! modules.isEmpty()) {
			for (final String moduleId : modules.split(",")) {
				AuthenticationUtil.runAs(
						new AuthenticationUtil.RunAsWork<Void>() {
							@Override
							public Void doWork() throws Exception {
								resetModuleVersion(moduleId);
								return null;
							}
						}, AuthenticationUtil.getSystemUserName());
			}
		}
	}
	
	private void resetModuleVersion(String moduleId) {
		// Get the module details from the registry
		RegistryKey moduleKeyCurrentVersion = new RegistryKey(
				ModuleComponentHelper.URI_MODULES_1_0,
				REGISTRY_PATH_MODULES, moduleId, REGISTRY_PROPERTY_CURRENT_VERSION);
		ModuleVersionNumber moduleCurrentVersion = getModuleVersionNumber(registryService.getProperty(moduleKeyCurrentVersion));
		
		
		String moduleVersionKey = "owsi.reset-module-version." + moduleId;
		String moduleNewVersionString = globalProperties.getProperty(moduleVersionKey);
		if (moduleNewVersionString != null){
			ModuleVersionNumber moduleNewVersion = getModuleVersionNumber(moduleNewVersionString);
			if (! moduleCurrentVersion.equals(moduleNewVersion)) {
				LOGGER.warn("Hack: Downgrade module " + moduleId + " from the version " + moduleCurrentVersion + " to the version " + moduleNewVersion + "!!!");
				registryService.addProperty(moduleKeyCurrentVersion, moduleNewVersion);
			} else {
				LOGGER.error("Hack: you should remove the property " + moduleVersionKey + " since the module in database is already in your target version " + moduleNewVersionString + "!!!");
			}
		} else {
			throw new IllegalStateException("You should specify the target version in the property : " + moduleVersionKey + "=<version>");
		}
	}
	
	private ModuleVersionNumber getModuleVersionNumber(Serializable moduleVersion) {
		if (moduleVersion instanceof ModuleVersionNumber) return (ModuleVersionNumber) moduleVersion;
		if (moduleVersion instanceof VersionNumber) return new ModuleVersionNumber((VersionNumber)moduleVersion);
		if (moduleVersion instanceof String) return new ModuleVersionNumber((String)moduleVersion);
		throw new ModuleManagementToolException("Invalid moduleVersion");
	}
	

	public void setGlobalProperties(Properties globalProperties) {
		this.globalProperties = globalProperties;
	}
	public void setRegistryService(RegistryService registryService) {
		this.registryService = registryService;
	}
	
	public void setModules(String modules) {
		this.modules = modules;
	}
}
