package fr.openwide.alfresco.component.model.repository.model.cm;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmStoreSelector extends AspectModel {

	public CmStoreSelector() {
		super(NameReference.create(CmModel.NAMESPACE, "storeSelector"));
	}

	protected CmStoreSelector(NameReference nameReference) {
		super(nameReference);
	}

	public final TextPropertyModel storeName = PropertyModels.newText(this, CmModel.NAMESPACE, "storeName");
}
