package fr.openwide.alfresco.component.model.repository.model.cm;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmClassifiable extends AspectModel {

	public CmClassifiable() {
		super(NameReference.create(CmModel.NAMESPACE, "classifiable"));
	}

	protected CmClassifiable(NameReference nameReference) {
		super(nameReference);
	}

}
