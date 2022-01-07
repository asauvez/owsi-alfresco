package fr.openwide.alfresco.component.model.repository.model.cm;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.DateTimePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.FloatPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmRating extends CmObject {

	public CmRating() {
		super(NameReference.create(CmModel.NAMESPACE, "rating"));
	}

	protected CmRating(NameReference nameReference) {
		super(nameReference);
	}
	
	public final FloatPropertyModel ratingScore = PropertyModels.newFloat(this, CmModel.NAMESPACE, "ratingScore");
	public final TextPropertyModel ratingScheme = PropertyModels.newText(this, CmModel.NAMESPACE, "ratingScheme");
	public final DateTimePropertyModel ratedAt = PropertyModels.newDateTime(this, CmModel.NAMESPACE, "ratedAt");
}
