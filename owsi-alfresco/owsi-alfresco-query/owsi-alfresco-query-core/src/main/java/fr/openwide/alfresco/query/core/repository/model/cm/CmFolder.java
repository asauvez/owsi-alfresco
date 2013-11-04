package fr.openwide.alfresco.query.core.repository.model.cm;

import fr.openwide.alfresco.query.core.node.model.property.PropertyModels;
import fr.openwide.alfresco.query.core.node.model.property.TextPropertyModel;
import fr.openwide.alfresco.query.core.node.model.value.NameReference;
import fr.openwide.alfresco.query.core.repository.model.CmModel;

public class CmFolder extends CmObject {

	public CmFolder() {
		super(NameReference.create(CmModel.NAMESPACE, "folder"));
	}

	protected CmFolder(NameReference nameReference) {
		super(nameReference);
	}

	public final TextPropertyModel name = PropertyModels.newText(this, CmModel.NAMESPACE, "name");

}
