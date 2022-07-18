package fr.openwide.alfresco.component.model.repository.model.cm;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.NodeReferencePropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmLink extends CmObject {

	public CmLink() {
		super(NameReference.create(CmModel.NAMESPACE, "link"));
	}

	protected CmLink(NameReference nameReference) {
		super(nameReference);
	}

	public final NodeReferencePropertyModel destination = PropertyModels.newNodeReference(this, CmModel.NAMESPACE, "destination");
}
