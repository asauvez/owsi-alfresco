package fr.openwide.alfresco.component.model.repository.model.cm;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.DateTimePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.IntegerPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmFailedThumbnail extends CmObject {

	public CmFailedThumbnail() {
		super(NameReference.create(CmModel.NAMESPACE, "failedThumbnail"));
	}

	protected CmFailedThumbnail(NameReference nameReference) {
		super(nameReference);
	}
	
	public final IntegerPropertyModel failureCount = PropertyModels.newInteger(this, CmModel.NAMESPACE, "failureCount");
	public final DateTimePropertyModel failedThumbnailTime = PropertyModels.newDateTime(this, CmModel.NAMESPACE, "failedThumbnailTime");
}
