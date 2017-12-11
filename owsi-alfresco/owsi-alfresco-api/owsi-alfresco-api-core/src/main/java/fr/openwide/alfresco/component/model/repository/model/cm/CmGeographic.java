package fr.openwide.alfresco.component.model.repository.model.cm;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.DoublePropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmGeographic extends AspectModel {

	public CmGeographic() {
		super(NameReference.create(CmModel.NAMESPACE, "geographic"));
	}

	protected CmGeographic(NameReference nameReference) {
		super(nameReference);
	}

	public final DoublePropertyModel latitude = PropertyModels.newDouble(this, CmModel.NAMESPACE, "latitude");

	public final DoublePropertyModel longitude = PropertyModels.newDouble(this, CmModel.NAMESPACE, "longitude");

}
