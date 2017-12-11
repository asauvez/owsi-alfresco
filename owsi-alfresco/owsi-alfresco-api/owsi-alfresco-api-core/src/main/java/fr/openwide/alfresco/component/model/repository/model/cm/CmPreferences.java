package fr.openwide.alfresco.component.model.repository.model.cm;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.ContentPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmPreferences extends AspectModel{

	public CmPreferences() {
		super(NameReference.create(CmModel.NAMESPACE, "preferences"));
	}

	protected CmPreferences(NameReference nameReference) {
		super(nameReference);
	}
	
	public final ContentPropertyModel preferenceValues = PropertyModels.newContent(this, CmModel.NAMESPACE, "preferenceValues");

	public final ChildAssociationModel preferenceImage = new ChildAssociationModel(NameReference.create(CmModel.NAMESPACE, "preferenceImage"));
	
}
