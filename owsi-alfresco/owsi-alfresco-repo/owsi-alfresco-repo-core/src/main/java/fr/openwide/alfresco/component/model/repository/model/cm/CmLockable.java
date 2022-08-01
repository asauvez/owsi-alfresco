package fr.openwide.alfresco.component.model.repository.model.cm;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.BooleanPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.DatePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmLockable extends AspectModel {

	public CmLockable() {
		super(CmModel.NAMESPACE.createQName("lockable"));
	}

	protected CmLockable(QName qName) {
		super(qName);
	}

	public final TextPropertyModel lockOwner = PropertyModels.newText(this, CmModel.NAMESPACE, "lockOwner");

	public final TextPropertyModel lockType = PropertyModels.newText(this, CmModel.NAMESPACE, "lockType");

	public final DatePropertyModel expiryDate = PropertyModels.newDate(this, CmModel.NAMESPACE, "expiryDate");
	
	public final BooleanPropertyModel lockIsDeep = PropertyModels.newBoolean(this, CmModel.NAMESPACE, "lockIsDeep");

}
