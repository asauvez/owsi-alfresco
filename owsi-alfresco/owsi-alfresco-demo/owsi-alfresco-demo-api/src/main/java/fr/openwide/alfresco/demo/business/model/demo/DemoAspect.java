package fr.openwide.alfresco.demo.business.model.demo;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.DatePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.demo.business.model.DemoModel;

public class DemoAspect extends AspectModel {

	public DemoAspect() {
		super(NameReference.create(DemoModel.NAMESPACE, "demoAspect"));
	}

	protected DemoAspect(NameReference nameReference) {
		super(nameReference);
	}
	
	public final TextPropertyModel demoProperty = PropertyModels.newText(this, DemoModel.NAMESPACE, "demoProperty");
	
}
