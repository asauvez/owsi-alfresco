package fr.openwide.alfresco.component.model.repository.model.st;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.StModel;

public class StCustomSiteProperties extends AspectModel {
	
	public StCustomSiteProperties() {
		super(StModel.NAMESPACE.createQName("customsiteproperties"));
	}

	protected StCustomSiteProperties(QName qName) {
		super(qName);
	}

	public final TextPropertyModel additionalInformation = PropertyModels.newText(this, StModel.CUSTOM_PROPERTY_NAMESPACE, "additionalInformation");
}
