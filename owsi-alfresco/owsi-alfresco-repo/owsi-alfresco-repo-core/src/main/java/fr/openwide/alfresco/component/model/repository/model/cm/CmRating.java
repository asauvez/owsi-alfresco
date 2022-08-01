package fr.openwide.alfresco.component.model.repository.model.cm;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.DateTimePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.FloatPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmRating extends CmObject {

	public CmRating() {
		super(CmModel.NAMESPACE.createQName("rating"));
	}

	protected CmRating(QName qName) {
		super(qName);
	}
	
	public final FloatPropertyModel ratingScore = PropertyModels.newFloat(this, CmModel.NAMESPACE, "ratingScore");
	public final TextPropertyModel ratingScheme = PropertyModels.newText(this, CmModel.NAMESPACE, "ratingScheme");
	public final DateTimePropertyModel ratedAt = PropertyModels.newDateTime(this, CmModel.NAMESPACE, "ratedAt");
}
