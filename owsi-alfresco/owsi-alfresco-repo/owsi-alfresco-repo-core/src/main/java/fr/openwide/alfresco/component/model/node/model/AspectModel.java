package fr.openwide.alfresco.component.model.node.model;

import org.alfresco.service.namespace.QName;

public class AspectModel extends ContainerModel {

	public AspectModel(QName qName) {
		super(qName);
	}

	public AspectModel(AspectModel aspectModel, QName qName) {
		this(qName);
		copy(aspectModel, this);
	}
	
	public String getXmlModel() throws Exception {
		return getXmlModel("aspect");
	}
}
