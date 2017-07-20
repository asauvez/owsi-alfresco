package fr.openwide.alfresco.component.model.repository.model.rn;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.NameReferencePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.component.model.repository.model.cm.CmContent;

public class CmThumbnail extends CmContent {

	public CmThumbnail() {
		super(NameReference.create(CmModel.NAMESPACE, "thumbnail"));
	}

	protected CmThumbnail(NameReference nameReference) {
		super(nameReference);
	}

	public final TextPropertyModel thumbnailName = PropertyModels.newText(this, CmModel.NAMESPACE, "thumbnailName");

	public final NameReferencePropertyModel contentPropertyName = PropertyModels.newNameReference(this, CmModel.NAMESPACE, "contentPropertyName");
}
