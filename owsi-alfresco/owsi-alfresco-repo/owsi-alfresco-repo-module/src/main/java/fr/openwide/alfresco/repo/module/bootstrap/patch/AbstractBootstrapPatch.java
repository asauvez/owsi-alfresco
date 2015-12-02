package fr.openwide.alfresco.repo.module.bootstrap.patch;

import org.alfresco.repo.module.AbstractModuleComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.alfresco.api.module.identification.service.IdentificationService;
import fr.openwide.alfresco.repo.dictionary.node.service.NodeModelRepositoryService;
import fr.openwide.alfresco.repo.module.bootstrap.service.BootstrapService;

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
	
	protected BootstrapService bootstrapService;
	protected NodeModelRepositoryService nodeModelService;
	protected IdentificationService identificationService;

	public void setBootstrapService(BootstrapService bootstrapService) {
		this.bootstrapService = bootstrapService;
	}
	public void setNodeModelService(NodeModelRepositoryService nodeModelService) {
		this.nodeModelService = nodeModelService;
	}
	public void setIdentificationService(IdentificationService identificationService) {
		this.identificationService = identificationService;
	}

	protected String getModuleConfigPath() {
		return "alfresco/module/" + getModuleId();
	}
}
