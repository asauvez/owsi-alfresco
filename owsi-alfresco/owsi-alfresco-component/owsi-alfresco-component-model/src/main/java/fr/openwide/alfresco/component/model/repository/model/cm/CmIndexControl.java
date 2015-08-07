package fr.openwide.alfresco.component.model.repository.model.cm;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.BooleanPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmIndexControl extends AspectModel {

	public CmIndexControl() {
		super(NameReference.create(CmModel.NAMESPACE, "indexControl"));
	}

	protected CmIndexControl(NameReference nameReference) {
		super(nameReference);
	}

	public final BooleanPropertyModel isIndexed = PropertyModels.newBoolean(this, CmModel.NAMESPACE, "isIndexed");

	public final BooleanPropertyModel isContentIndexed = PropertyModels.newBoolean(this, CmModel.NAMESPACE, "isContentIndexed");

}
