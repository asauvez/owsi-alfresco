package fr.openwide.alfresco.component.model.repository.model.cm;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiNodeRefPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmTaggable extends AspectModel{

	public CmTaggable() {
		super(CmModel.NAMESPACE.createQName("taggable"));
	}

	protected CmTaggable(QName qName) {
		super(qName);
	}
	
	public final MultiNodeRefPropertyModel taggable = PropertyModels.newMultiNodeRef(this, CmModel.NAMESPACE, "taggable");
	
}
