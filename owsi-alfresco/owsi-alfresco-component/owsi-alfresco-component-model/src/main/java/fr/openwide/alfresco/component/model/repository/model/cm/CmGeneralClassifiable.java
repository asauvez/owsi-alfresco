package fr.openwide.alfresco.component.model.repository.model.cm;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiTextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmGeneralClassifiable extends AspectModel {

	public CmGeneralClassifiable() {
		super(NameReference.create(CmModel.NAMESPACE, "generalclassifiable"));
	}

	protected CmGeneralClassifiable(NameReference nameReference) {
		super(nameReference);
	}

	// TODO vrai type = d:category
	public final MultiTextPropertyModel categories = PropertyModels.newMultiText(this, CmModel.NAMESPACE, "categories");
}
