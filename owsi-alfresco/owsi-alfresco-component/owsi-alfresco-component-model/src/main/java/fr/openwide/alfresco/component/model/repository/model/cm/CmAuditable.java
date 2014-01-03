package fr.openwide.alfresco.component.model.repository.model.cm;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
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

	public final DateTimePropertyModel created = PropertyModels.newDateTime(this, CmModel.NAMESPACE, "created");

	public final TextPropertyModel creator = PropertyModels.newText(this, CmModel.NAMESPACE, "creator");

	public final DateTimePropertyModel modified = PropertyModels.newDateTime(this, CmModel.NAMESPACE, "modified");

	public final TextPropertyModel modifier = PropertyModels.newText(this, CmModel.NAMESPACE, "modifier");

	public final DateTimePropertyModel accessed = PropertyModels.newDateTime(this, CmModel.NAMESPACE, "accessed");

}
