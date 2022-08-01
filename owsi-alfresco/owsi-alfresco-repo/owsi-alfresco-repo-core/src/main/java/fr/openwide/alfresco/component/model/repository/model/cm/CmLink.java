package fr.openwide.alfresco.component.model.repository.model.cm;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.NodeRefPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmLink extends CmObject {

	public CmLink() {
		super(CmModel.NAMESPACE.createQName("link"));
	}

	protected CmLink(QName qName) {
		super(qName);
	}

	public final NodeRefPropertyModel destination = PropertyModels.newNodeRef(this, CmModel.NAMESPACE, "destination");
}
