package fr.openwide.alfresco.component.model.repository.model.cm;

import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.BooleanPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.DateTimePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.LongPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.RefPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class CmPerson extends CmAuthority {

	public CmPerson() {
		super(NameReference.create(CmModel.NAMESPACE, "person"));
	}

	protected CmPerson(NameReference nameReference) {
		super(nameReference);
	}

	public final TextPropertyModel userName = PropertyModels.newText(this, CmModel.NAMESPACE, "userName");
	public final TextPropertyModel firstName = PropertyModels.newText(this, CmModel.NAMESPACE, "firstName");
	public final TextPropertyModel lastName = PropertyModels.newText(this, CmModel.NAMESPACE, "lastName");
	public final TextPropertyModel middleName = PropertyModels.newText(this, CmModel.NAMESPACE, "middleName");
	public final TextPropertyModel email = PropertyModels.newText(this, CmModel.NAMESPACE, "email");
	public final RefPropertyModel homeFolder = PropertyModels.newRef(this, CmModel.NAMESPACE, "homeFolder");
	public final DateTimePropertyModel userStatusTime = PropertyModels.newDateTime(this, CmModel.NAMESPACE, "userStatusTime");
	public final LongPropertyModel emailFeedId = PropertyModels.newLong(this, CmModel.NAMESPACE, "emailFeedId");
	public final BooleanPropertyModel subscriptionsPrivate = PropertyModels.newBoolean(this, CmModel.NAMESPACE, "subscriptionsPrivate");

}
