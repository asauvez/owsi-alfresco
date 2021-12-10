package fr.openwide.alfresco.component.model.repository.model.rn;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.IntegerPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.RnModel;

public class RnRendition2 extends AspectModel {

	public RnRendition2() {
		super(NameReference.create(RnModel.NAMESPACE, "rendition"));
	}

	protected RnRendition2(NameReference nameReference) {
		super(nameReference);
	}

	public final IntegerPropertyModel contentUrlHashCode = PropertyModels.newInteger(this, RnModel.NAMESPACE, "contentUrlHashCode");
}
