package fr.openwide.alfresco.component.model.repository.model.cm;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmStoreSelector extends AspectModel {

	public CmStoreSelector() {
		super(CmModel.NAMESPACE.createQName("storeSelector"));
	}

	protected CmStoreSelector(QName qName) {
		super(qName);
	}

	public final TextPropertyModel storeName = PropertyModels.newText(this, CmModel.NAMESPACE, "storeName");
}
