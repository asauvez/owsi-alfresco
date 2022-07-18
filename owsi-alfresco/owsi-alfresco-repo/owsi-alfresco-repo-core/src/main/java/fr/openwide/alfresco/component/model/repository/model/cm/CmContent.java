package fr.openwide.alfresco.component.model.repository.model.cm;

import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.ContentPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class CmContent extends CmObject {

	public CmContent() {
		super(NameReference.create(CmModel.NAMESPACE, "content"));
	}

	protected CmContent(NameReference nameReference) {
		super(nameReference);
	}

	public final ContentPropertyModel content = PropertyModels.newContent(this, CmModel.NAMESPACE, "content");

}
