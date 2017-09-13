package fr.openwide.alfresco.api.module.model.owsi;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.module.model.OwsiModel;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.DateTimePropertyModel;

public class OwsiClassifiable extends AspectModel {

	public OwsiClassifiable() {
		super(NameReference.create(OwsiModel.NAMESPACE, "classifiable"));
	}

	protected OwsiClassifiable(NameReference nameReference) {
		super(nameReference);
	}

	public final DateTimePropertyModel classificationDate = PropertyModels.newDateTime(this, OwsiModel.NAMESPACE, "classificationDate");
}
