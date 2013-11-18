package fr.openwide.alfresco.query.core.repository.model.cm;

import fr.openwide.alfresco.query.api.node.model.NameReference;
import fr.openwide.alfresco.query.core.node.model.property.ContentPropertyModel;
import fr.openwide.alfresco.query.core.node.model.property.PropertyModels;
import fr.openwide.alfresco.query.core.repository.model.CmModel;

public class CmContent extends CmObject {

	public CmContent() {
		super(NameReference.create(CmModel.NAMESPACE, "content"));
	}

	protected CmContent(NameReference nameReference) {
		super(nameReference);
	}

	public final ContentPropertyModel content = PropertyModels.newContent(this, CmModel.NAMESPACE, "content");

}
