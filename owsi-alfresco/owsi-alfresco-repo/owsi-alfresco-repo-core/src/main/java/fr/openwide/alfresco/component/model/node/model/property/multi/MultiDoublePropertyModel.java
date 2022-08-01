package fr.openwide.alfresco.component.model.node.model.property.multi;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;

public class MultiDoublePropertyModel extends AbstractMultiNumberPropertyModel<Double> {

	public MultiDoublePropertyModel(ContainerModel type, QName qName) {
		super(type, qName);
	}

	@Override
	public Class<Double> getValueClass() {
		return Double.class;
	}
	
	@Override
	public String getDataType() {
		return "d:double";
	}

}
