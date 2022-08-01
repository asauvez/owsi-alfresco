package fr.openwide.alfresco.component.model.repository.model.rn;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.QNamePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.component.model.repository.model.cm.CmContent;

public class CmThumbnail extends CmContent {

	public CmThumbnail() {
		super(CmModel.NAMESPACE.createQName("thumbnail"));
	}

	protected CmThumbnail(QName qName) {
		super(qName);
	}

	public final TextPropertyModel thumbnailName = PropertyModels.newText(this, CmModel.NAMESPACE, "thumbnailName");

	public final QNamePropertyModel contentPropertyName = PropertyModels.newQName(this, CmModel.NAMESPACE, "contentPropertyName");
}
