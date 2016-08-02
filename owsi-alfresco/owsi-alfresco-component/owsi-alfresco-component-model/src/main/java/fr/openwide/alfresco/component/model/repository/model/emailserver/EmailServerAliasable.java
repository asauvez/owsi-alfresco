package fr.openwide.alfresco.component.model.repository.model.emailserver;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.EmailServerModel;

public class EmailServerAliasable extends AspectModel {
	
	public EmailServerAliasable() {
		super(NameReference.create(EmailServerModel.NAMESPACE, "aliasable"));
	}

	protected EmailServerAliasable(NameReference nameReference) {
		super(nameReference);
	}

	public final TextPropertyModel alias = PropertyModels.newText(this, EmailServerModel.NAMESPACE, "alias");
}
