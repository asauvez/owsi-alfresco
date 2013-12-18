package fr.openwide.alfresco.query.core.repository.model.cm;

import fr.openwide.alfresco.query.core.node.model.AspectModel;
import fr.openwide.alfresco.query.core.node.model.property.PropertyModels;
import fr.openwide.alfresco.query.core.node.model.property.TextPropertyModel;
import fr.openwide.alfresco.query.core.repository.model.CmModel;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class CmOwnable extends AspectModel {

	public CmOwnable() {
		super(NameReference.create(CmModel.NAMESPACE, "ownable"));
	}

	protected CmOwnable(NameReference nameReference) {
		super(nameReference);
	}

	public final TextPropertyModel owner = PropertyModels.newText(this, CmModel.NAMESPACE, "owner");

}
