package fr.openwide.alfresco.component.model.node.model.property.multi;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;

public class MultiFloatPropertyModel extends AbstractMultiNumberPropertyModel<Float> {

	public MultiFloatPropertyModel(ContainerModel type, QName qName) {
		super(type, qName);
	}

	@Override
	public Class<Float> getValueClass() {
		return Float.class;
	}

	@Override
	public String getDataType() {
		return "d:float";
	}
}
