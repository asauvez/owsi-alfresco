package fr.openwide.alfresco.component.model.repository.model.cm;

import fr.openwide.alfresco.component.model.node.model.constraint.FileNamePropertyConstraint;
import fr.openwide.alfresco.component.model.node.model.constraint.MandatoryPropertyConstraint;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.component.model.repository.model.sys.SysBase;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class CmObject extends SysBase {

	public CmObject() {
		super(NameReference.create(CmModel.NAMESPACE, "cmobject"));
	}

	protected CmObject(NameReference nameReference) {
		super(nameReference);
	}

	public final TextPropertyModel name = PropertyModels.newText(this, "name",
			MandatoryPropertyConstraint.INSTANCE,
			FileNamePropertyConstraint.INSTANCE);

	public final CmAuditable auditable = addMandatoryAspect(CmModel.auditable);
}
