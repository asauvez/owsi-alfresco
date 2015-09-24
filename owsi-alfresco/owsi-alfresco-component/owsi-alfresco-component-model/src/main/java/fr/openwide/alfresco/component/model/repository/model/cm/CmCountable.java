package fr.openwide.alfresco.component.model.repository.model.cm;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.IntegerPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmCountable extends AspectModel {

	public CmCountable() {
		super(NameReference.create(CmModel.NAMESPACE, "countable"));
	}

	protected CmCountable(NameReference nameReference) {
		super(nameReference);
	}

	public final IntegerPropertyModel hits = PropertyModels.newInteger(this, CmModel.NAMESPACE, "hits");

	public final IntegerPropertyModel counter = PropertyModels.newInteger(this, CmModel.NAMESPACE, "counter");

}
