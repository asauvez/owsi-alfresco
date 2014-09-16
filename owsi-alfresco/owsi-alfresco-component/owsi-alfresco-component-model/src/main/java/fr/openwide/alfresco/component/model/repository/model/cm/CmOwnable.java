package fr.openwide.alfresco.component.model.repository.model.cm;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class CmOwnable extends AspectModel {

	public CmOwnable() {
		super(NameReference.create(CmModel.NAMESPACE, "ownable"));
	}

	protected CmOwnable(NameReference nameReference) {
		super(nameReference);
	}

	public final TextPropertyModel owner = PropertyModels.newText(this, "owner");

}
