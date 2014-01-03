package fr.openwide.alfresco.component.model.repository.model.cm;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class CmTitled extends AspectModel {

	public CmTitled() {
		super(NameReference.create(CmModel.NAMESPACE, "titled"));
	}

	protected CmTitled(NameReference nameReference) {
		super(nameReference);
	}

	public final TextPropertyModel title = PropertyModels.newText(this, CmModel.NAMESPACE, "title");
	
	public final TextPropertyModel description = PropertyModels.newText(this, CmModel.NAMESPACE, "description");

}
