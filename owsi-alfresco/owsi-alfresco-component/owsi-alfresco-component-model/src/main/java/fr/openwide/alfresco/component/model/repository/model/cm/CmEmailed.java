package fr.openwide.alfresco.component.model.repository.model.cm;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiTextPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.DateTimePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmEmailed extends AspectModel{

	public CmEmailed() {
		super(NameReference.create(CmModel.NAMESPACE, "emailed"));
	}

	protected CmEmailed(NameReference nameReference) {
		super(nameReference);
	}
	
	public final TextPropertyModel originator = PropertyModels.newText(this, CmModel.NAMESPACE, "originator");
	public final TextPropertyModel addressee = PropertyModels.newText(this, CmModel.NAMESPACE, "addressee");
	public final MultiTextPropertyModel addressees = PropertyModels.newMultiText(this, CmModel.NAMESPACE, "addressees");
	public final TextPropertyModel subjectLine = PropertyModels.newText(this, CmModel.NAMESPACE, "subjectline");
	public final DateTimePropertyModel sentDate = PropertyModels.newDateTime(this, CmModel.NAMESPACE, "sentdate");
	
}
