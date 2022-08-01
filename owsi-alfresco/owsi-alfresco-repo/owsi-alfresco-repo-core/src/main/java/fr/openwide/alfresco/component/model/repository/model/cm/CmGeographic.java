package fr.openwide.alfresco.component.model.repository.model.cm;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.DoublePropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmGeographic extends AspectModel {

	public CmGeographic() {
		super(CmModel.NAMESPACE.createQName("geographic"));
	}

	protected CmGeographic(QName qName) {
		super(qName);
	}

	public final DoublePropertyModel latitude = PropertyModels.newDouble(this, CmModel.NAMESPACE, "latitude");

	public final DoublePropertyModel longitude = PropertyModels.newDouble(this, CmModel.NAMESPACE, "longitude");

}
