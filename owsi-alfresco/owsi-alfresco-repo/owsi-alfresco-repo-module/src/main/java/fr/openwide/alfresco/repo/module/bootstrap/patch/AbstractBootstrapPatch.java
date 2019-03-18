package fr.openwide.alfresco.repo.module.bootstrap.patch;

import org.alfresco.repo.module.AbstractModuleComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import fr.openwide.alfresco.api.module.identification.service.IdentificationService;
import fr.openwide.alfresco.repo.dictionary.node.service.NodeModelRepositoryService;
import fr.openwide.alfresco.repo.module.bootstrap.service.BootstrapService;
import fr.openwide.alfresco.repo.remote.conversion.service.ConversionService;

/**
 * Base des patchs utilisant BootstrapService.
 * 
 * Pour déclarer un patch en XML :
 * <bean id="[...]" class="[...]" parent="owsi.patch.parent">
 * 		<property name="moduleId" value="${alfresco.module.name}" />
 * </bean>
 */
public abstract class AbstractBootstrapPatch extends AbstractModuleComponent {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired protected BootstrapService bootstrapService;
	@Autowired protected NodeModelRepositoryService nodeModelService;
	@Autowired protected IdentificationService identificationService;
	@Autowired protected ConversionService conversionService;

	protected String getModuleConfigPath() {
		return "alfresco/module/" + getModuleId();
	}
}
