package fr.openwide.alfresco.component.model.repository.model.usr;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.BooleanPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.DateTimePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.component.model.repository.model.UsrModel;

public class UsrUser extends UsrAuthority {

	public UsrUser() {
		super(NameReference.create(UsrModel.NAMESPACE, "user"));
	}

	protected UsrUser(NameReference nameReference) {
		super(nameReference);
	}
	
	public final TextPropertyModel username = PropertyModels.newText(this, CmModel.NAMESPACE, "username");
	public final TextPropertyModel password = PropertyModels.newText(this, CmModel.NAMESPACE, "password");
	public final BooleanPropertyModel enabled = PropertyModels.newBoolean(this, CmModel.NAMESPACE, "enabled");
	public final BooleanPropertyModel accountExpires = PropertyModels.newBoolean(this, CmModel.NAMESPACE, "accountExpires");
	public final DateTimePropertyModel accountExpiryDate = PropertyModels.newDateTime(this, CmModel.NAMESPACE, "accountExpiryDate");
	public final BooleanPropertyModel credentialsExpire = PropertyModels.newBoolean(this, CmModel.NAMESPACE, "credentialsExpire");
	public final DateTimePropertyModel credentialsExpiryDate = PropertyModels.newDateTime(this, CmModel.NAMESPACE, "credentialsExpiryDate");
	public final BooleanPropertyModel accountLocked = PropertyModels.newBoolean(this, CmModel.NAMESPACE, "accountLocked");
	public final TextPropertyModel salt = PropertyModels.newText(this, CmModel.NAMESPACE, "salt");

}
