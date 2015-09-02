package fr.openwide.alfresco.api.module.model.owsi;


import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.module.model.OwsiModel;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.NameReferencePropertyModel;

public class OwsiIdentifiable extends AspectModel {

	public OwsiIdentifiable() {
		super(NameReference.create(OwsiModel.NAMESPACE, "identifiable"));
	}

	protected OwsiIdentifiable(NameReference nameReference) {
		super(nameReference);
	}
	
	public final NameReferencePropertyModel identifier = PropertyModels.newNameReference(this, OwsiModel.NAMESPACE, "identifier");

}
