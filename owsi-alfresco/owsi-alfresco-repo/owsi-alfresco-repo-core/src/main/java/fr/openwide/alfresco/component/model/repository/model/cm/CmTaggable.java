package fr.openwide.alfresco.component.model.repository.model.cm;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiNodeReferencePropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmTaggable extends AspectModel{

	public CmTaggable() {
		super(NameReference.create(CmModel.NAMESPACE, "taggable"));
	}

	protected CmTaggable(NameReference nameReference) {
		super(nameReference);
	}
	
	public final MultiNodeReferencePropertyModel taggable = PropertyModels.newMultiNodeReference(this, CmModel.NAMESPACE, "taggable");
	
}
