package fr.openwide.alfresco.component.model.repository.model.sys;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.constraint.MandatoryEnforcedPropertyConstraint;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.DateTimePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.SysModel;

public class SysArchived extends AspectModel {

	public SysArchived() {
		super(SysModel.NAMESPACE.createQName("archived"));
	}

	protected SysArchived(QName qName) {
		super(qName);
	}

//	public final ChildAssocRefPropertyModel archivedOriginalParentAssoc = PropertyModels.newChildAssocRef(this, SysModel.NAMESPACE, "archivedOriginalParentAssoc",
//			MandatoryEnforcedPropertyConstraint.INSTANCE);

	public final TextPropertyModel archivedBy = PropertyModels.newText(this, SysModel.NAMESPACE, "archivedBy",
			MandatoryEnforcedPropertyConstraint.INSTANCE);

	public final DateTimePropertyModel archivedDate = PropertyModels.newDateTime(this, SysModel.NAMESPACE, "archivedDate",
			MandatoryEnforcedPropertyConstraint.INSTANCE);

	public final TextPropertyModel archivedOriginalOwner = PropertyModels.newText(this, SysModel.NAMESPACE, "archivedOriginalOwner",
			MandatoryEnforcedPropertyConstraint.INSTANCE);

}
