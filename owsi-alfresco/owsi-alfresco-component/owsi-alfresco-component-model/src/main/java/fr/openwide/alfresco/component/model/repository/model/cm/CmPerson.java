package fr.openwide.alfresco.component.model.repository.model.cm;

import fr.openwide.alfresco.component.model.node.model.AssociationModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.BooleanPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.ContentPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.DateTimePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.LongPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.NodeReferencePropertyModel;
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

	public final TextPropertyModel userName = PropertyModels.newText(this, "userName");
	public final NodeReferencePropertyModel homeFolder = PropertyModels.newNodeReference(this, "homeFolder");
	public final TextPropertyModel firstName = PropertyModels.newText(this, "firstName");
	public final TextPropertyModel lastName = PropertyModels.newText(this, "lastName");
	public final TextPropertyModel middleName = PropertyModels.newText(this, "middleName");
	public final TextPropertyModel email = PropertyModels.newText(this, "email");
	public final TextPropertyModel organizationId = PropertyModels.newText(this, "organizationId");
	public final TextPropertyModel homeFolderProvider = PropertyModels.newText(this, "homeFolderProvider");
	public final TextPropertyModel defaultHomeFolderPath = PropertyModels.newText(this, "defaultHomeFolderPath");
	public final TextPropertyModel presenceProvider = PropertyModels.newText(this, "presenceProvider");
	public final TextPropertyModel presenceUsername = PropertyModels.newText(this, "presenceUsername");
	public final TextPropertyModel organization = PropertyModels.newText(this, "organization");
	public final TextPropertyModel jobtitle = PropertyModels.newText(this, "jobtitle");
	public final TextPropertyModel location = PropertyModels.newText(this, "location");
	public final ContentPropertyModel persondescription = PropertyModels.newContent(this, "persondescription");
	public final TextPropertyModel telephone = PropertyModels.newText(this, "telephone");
	public final TextPropertyModel mobile = PropertyModels.newText(this, "mobile");
	public final TextPropertyModel companyaddress1 = PropertyModels.newText(this, "companyaddress1");
	public final TextPropertyModel companyaddress2 = PropertyModels.newText(this, "companyaddress2");
	public final TextPropertyModel companyaddress3 = PropertyModels.newText(this, "companyaddress3");
	public final TextPropertyModel companypostcode = PropertyModels.newText(this, "companypostcode");
	public final TextPropertyModel companytelephone = PropertyModels.newText(this, "companytelephone");
	public final TextPropertyModel companyfax = PropertyModels.newText(this, "companyfax");
	public final TextPropertyModel companyemail = PropertyModels.newText(this, "companyemail");
	public final TextPropertyModel skype = PropertyModels.newText(this, "skype");
	public final TextPropertyModel instantmsg = PropertyModels.newText(this, "instantmsg");
	public final TextPropertyModel userStatus = PropertyModels.newText(this, "userStatus");
	public final DateTimePropertyModel userStatusTime = PropertyModels.newDateTime(this, "userStatusTime");
	public final TextPropertyModel googleusername = PropertyModels.newText(this, "googleusername");
	public final BooleanPropertyModel emailFeedDisabled = PropertyModels.newBoolean(this, "emailFeedDisabled");
	public final BooleanPropertyModel subscriptionsPrivate = PropertyModels.newBoolean(this, "subscriptionsPrivate");
	public final LongPropertyModel emailFeedId = PropertyModels.newLong(this, "emailFeedId");
	public final LongPropertyModel sizeCurrent = PropertyModels.newLong(this, "sizeCurrent");
	public final LongPropertyModel sizeQuota = PropertyModels.newLong(this, "sizeQuota");

	public final AssociationModel avatar = new AssociationModel(NameReference.create(CmModel.NAMESPACE, "avatar"));
}
