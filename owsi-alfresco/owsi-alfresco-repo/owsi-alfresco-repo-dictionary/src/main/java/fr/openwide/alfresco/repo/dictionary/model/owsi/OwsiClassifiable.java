package fr.openwide.alfresco.repo.dictionary.model.owsi;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.repo.dictionary.model.OwsiModel;

public class OwsiClassifiable extends AspectModel {

	public OwsiClassifiable() {
		super(NameReference.create(OwsiModel.NAMESPACE, "classifiable"));
	}

	protected OwsiClassifiable(NameReference nameReference) {
		super(nameReference);
	}

}
