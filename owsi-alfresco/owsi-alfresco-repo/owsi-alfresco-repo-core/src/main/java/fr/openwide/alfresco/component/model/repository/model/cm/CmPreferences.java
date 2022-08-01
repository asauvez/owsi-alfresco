package fr.openwide.alfresco.component.model.repository.model.cm;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.ContentPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmPreferences extends AspectModel{

	public CmPreferences() {
		super(CmModel.NAMESPACE.createQName("preferences"));
	}

	protected CmPreferences(QName qName) {
		super(qName);
	}
	
	public final ContentPropertyModel preferenceValues = PropertyModels.newContent(this, CmModel.NAMESPACE, "preferenceValues");

	public final ChildAssociationModel preferenceImage = new ChildAssociationModel(CmModel.NAMESPACE.createQName("preferenceImage"));
	
}
