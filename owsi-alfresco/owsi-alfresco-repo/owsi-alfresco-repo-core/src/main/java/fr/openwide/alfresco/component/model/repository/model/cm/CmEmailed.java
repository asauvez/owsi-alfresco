package fr.openwide.alfresco.component.model.repository.model.cm;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiTextPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.DateTimePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmEmailed extends AspectModel{

	public CmEmailed() {
		super(CmModel.NAMESPACE.createQName("emailed"));
	}

	protected CmEmailed(QName qName) {
		super(qName);
	}
	
	public final TextPropertyModel originator = PropertyModels.newText(this, CmModel.NAMESPACE, "originator");
	public final TextPropertyModel addressee = PropertyModels.newText(this, CmModel.NAMESPACE, "addressee");
	public final MultiTextPropertyModel addressees = PropertyModels.newMultiText(this, CmModel.NAMESPACE, "addressees");
	public final TextPropertyModel subjectLine = PropertyModels.newText(this, CmModel.NAMESPACE, "subjectline");
	public final DateTimePropertyModel sentDate = PropertyModels.newDateTime(this, CmModel.NAMESPACE, "sentdate");
	
}
