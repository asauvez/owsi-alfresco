package fr.openwide.alfresco.query.core.repository.model.cm;

import fr.openwide.alfresco.query.core.node.model.property.ContentPropertyModel;
import fr.openwide.alfresco.query.core.node.model.property.PropertyModels;
import fr.openwide.alfresco.query.core.repository.model.CmModel;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class CmContent extends CmObject {

	public CmContent() {
		super(NameReference.create(CmModel.NAMESPACE, "content"));
	}

	protected CmContent(NameReference nameReference) {
		super(nameReference);
	}

	public final ContentPropertyModel content = PropertyModels.newContent(this, CmModel.NAMESPACE, "content");

}
