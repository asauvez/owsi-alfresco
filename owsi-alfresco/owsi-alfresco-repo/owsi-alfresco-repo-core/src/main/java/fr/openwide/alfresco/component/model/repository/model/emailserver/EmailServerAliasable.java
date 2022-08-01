package fr.openwide.alfresco.component.model.repository.model.emailserver;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.EmailServerModel;

public class EmailServerAliasable extends AspectModel {
	
	public EmailServerAliasable() {
		super(EmailServerModel.NAMESPACE.createQName("aliasable"));
	}

	protected EmailServerAliasable(QName qName) {
		super(qName);
	}

	public final TextPropertyModel alias = PropertyModels.newText(this, EmailServerModel.NAMESPACE, "alias");
}
