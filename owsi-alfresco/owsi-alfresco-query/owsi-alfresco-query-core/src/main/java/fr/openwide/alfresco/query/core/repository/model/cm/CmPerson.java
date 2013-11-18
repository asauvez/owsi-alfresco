package fr.openwide.alfresco.query.core.repository.model.cm;

import fr.openwide.alfresco.query.api.node.model.NameReference;
import fr.openwide.alfresco.query.core.node.model.property.BooleanPropertyModel;
import fr.openwide.alfresco.query.core.node.model.property.DateTimePropertyModel;
import fr.openwide.alfresco.query.core.node.model.property.LongPropertyModel;
import fr.openwide.alfresco.query.core.node.model.property.PropertyModels;
import fr.openwide.alfresco.query.core.node.model.property.RefPropertyModel;
import fr.openwide.alfresco.query.core.node.model.property.TextPropertyModel;
import fr.openwide.alfresco.query.core.repository.model.CmModel;

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
