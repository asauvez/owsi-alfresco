package fr.openwide.alfresco.component.model.repository.model.cm;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.constraint.ProtectedPropertyConstraint;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.DateTimePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class CmAuditable extends AspectModel {

	public CmAuditable() {
		super(NameReference.create(CmModel.NAMESPACE, "auditable"));
	}

	protected CmAuditable(NameReference nameReference) {
		super(nameReference);
	}

	public final DateTimePropertyModel created = PropertyModels.newDateTime(this, "created",
			ProtectedPropertyConstraint.INSTANCE);

	public final TextPropertyModel creator = PropertyModels.newText(this, "creator",
			ProtectedPropertyConstraint.INSTANCE);

	public final DateTimePropertyModel modified = PropertyModels.newDateTime(this, "modified",
			ProtectedPropertyConstraint.INSTANCE);

	public final TextPropertyModel modifier = PropertyModels.newText(this, "modifier",
			ProtectedPropertyConstraint.INSTANCE);

	public final DateTimePropertyModel accessed = PropertyModels.newDateTime(this, "accessed",
			ProtectedPropertyConstraint.INSTANCE);

}
