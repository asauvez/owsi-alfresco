package fr.openwide.alfresco.demo.core.test;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.api.module.identification.service.IdentificationService;
import fr.openwide.alfresco.app.module.framework.spring.config.AppModuleServiceConfig;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.component.model.node.service.NodeModelService;
import fr.openwide.alfresco.demo.business.model.DemoModel;
import fr.openwide.alfresco.demo.core.test.framework.spring.AbstractAlfrescoIT;

@ContextConfiguration(classes=AppModuleServiceConfig.class)
public abstract class AbstractDemoIT extends AbstractAlfrescoIT {
	
	@Autowired
	protected NodeModelService nodeModelService;
	@Autowired
	protected IdentificationService identificationService;
	
	public NodeReference getRootFolder() {
		return identificationService.getByIdentifier(DemoModel.DEMO_ROOT_FOLDER).get();
	}

	@Before @After
	public void cleanRootFolder() {
		List<BusinessNode> children = nodeModelService.getChildren(getRootFolder(), new NodeScopeBuilder().nodeReference());
		for (BusinessNode child : children) {
			nodeModelService.delete(child.getNodeReference());
		}
	}
}
