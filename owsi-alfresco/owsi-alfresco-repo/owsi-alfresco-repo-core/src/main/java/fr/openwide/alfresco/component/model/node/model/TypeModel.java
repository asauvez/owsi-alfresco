package fr.openwide.alfresco.component.model.node.model;

import org.alfresco.service.namespace.QName;

public class TypeModel extends ContainerModel {

	public TypeModel(QName qName) {
		super(qName);
	}

	public TypeModel(TypeModel typeModel, QName qName) {
		this(qName);
		copy(typeModel, this);
	}
	
	public String getXmlModel() throws Exception {
		return getXmlModel("type");
	}
}
