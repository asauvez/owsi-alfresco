package fr.openwide.alfresco.component.model.repository.model.cm;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.constraint.MandatoryEnforcedPropertyConstraint;
import fr.openwide.alfresco.component.model.node.model.constraint.ProtectedPropertyConstraint;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class CmWorkingCopy extends AspectModel {

	public CmWorkingCopy() {
		super(NameReference.create(CmModel.NAMESPACE, "workingcopy"));
	}

	protected CmWorkingCopy(NameReference nameReference) {
		super(nameReference);
	}

	public final TextPropertyModel workingCopyOwner = PropertyModels.newText(this, CmModel.NAMESPACE, "workingCopyOwner",
			MandatoryEnforcedPropertyConstraint.INSTANCE,
			ProtectedPropertyConstraint.INSTANCE);

	public final TextPropertyModel workingCopyMode = PropertyModels.newText(this, CmModel.NAMESPACE, "workingCopyMode");

	public final TextPropertyModel workingCopyLabel = PropertyModels.newText(this, CmModel.NAMESPACE, "workingCopyLabel",
			ProtectedPropertyConstraint.INSTANCE);

}
