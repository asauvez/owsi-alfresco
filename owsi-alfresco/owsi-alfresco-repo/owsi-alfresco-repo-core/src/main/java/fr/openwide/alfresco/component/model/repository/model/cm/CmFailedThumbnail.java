package fr.openwide.alfresco.component.model.repository.model.cm;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.DateTimePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.IntegerPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmFailedThumbnail extends CmObject {

	public CmFailedThumbnail() {
		super(CmModel.NAMESPACE.createQName("failedThumbnail"));
	}

	protected CmFailedThumbnail(QName qName) {
		super(qName);
	}
	
	public final IntegerPropertyModel failureCount = PropertyModels.newInteger(this, CmModel.NAMESPACE, "failureCount");
	public final DateTimePropertyModel failedThumbnailTime = PropertyModels.newDateTime(this, CmModel.NAMESPACE, "failedThumbnailTime");
}
