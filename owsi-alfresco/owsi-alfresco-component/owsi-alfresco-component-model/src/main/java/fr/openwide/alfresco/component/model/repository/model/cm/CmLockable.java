package fr.openwide.alfresco.component.model.repository.model.cm;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.BooleanPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.DatePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmLockable extends AspectModel {

	public CmLockable() {
		super(NameReference.create(CmModel.NAMESPACE, "lockable"));
	}

	protected CmLockable(NameReference nameReference) {
		super(nameReference);
	}

	public final TextPropertyModel lockOwner = PropertyModels.newText(this, CmModel.NAMESPACE, "lockOwner");

	public final TextPropertyModel lockType = PropertyModels.newText(this, CmModel.NAMESPACE, "lockType");

	public final DatePropertyModel expiryDate = PropertyModels.newDate(this, CmModel.NAMESPACE, "expiryDate");
	
	public final BooleanPropertyModel lockIsDeep = PropertyModels.newBoolean(this, CmModel.NAMESPACE, "lockIsDeep");

}
