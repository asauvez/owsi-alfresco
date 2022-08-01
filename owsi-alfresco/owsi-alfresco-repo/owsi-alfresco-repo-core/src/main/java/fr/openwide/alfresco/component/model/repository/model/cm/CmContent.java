package fr.openwide.alfresco.component.model.repository.model.cm;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.ContentPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmContent extends CmObject {

	public CmContent() {
		super(CmModel.NAMESPACE.createQName("content"));
	}

	protected CmContent(QName qName) {
		super(qName);
	}

	public final ContentPropertyModel content = PropertyModels.newContent(this, CmModel.NAMESPACE, "content");

}
