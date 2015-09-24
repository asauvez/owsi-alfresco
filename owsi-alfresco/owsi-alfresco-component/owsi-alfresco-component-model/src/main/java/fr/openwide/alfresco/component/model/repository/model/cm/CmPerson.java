package fr.openwide.alfresco.component.model.repository.model.cm;

import fr.openwide.alfresco.component.model.node.model.AssociationModel;
import fr.openwide.alfresco.component.model.node.model.constraint.MandatoryEnforcedPropertyConstraint;
import fr.openwide.alfresco.component.model.node.model.constraint.ProtectedPropertyConstraint;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.BooleanPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.ContentPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.DateTimePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.LongPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.NodeReferencePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class CmPerson extends CmAuthority {

	public CmPerson() {
		super(NameReference.create(CmModel.NAMESPACE, "person"));
	}

	protected CmPerson(NameReference nameReference) {
		super(nameReference);
	}

	public final TextPropertyModel userName = PropertyModels.newText(this, CmModel.NAMESPACE, "userName",
			MandatoryEnforcedPropertyConstraint.INSTANCE);
	
	public final NodeReferencePropertyModel homeFolder = PropertyModels.newNodeReference(this, CmModel.NAMESPACE, "homeFolder");
	public final TextPropertyModel firstName = PropertyModels.newText(this, CmModel.NAMESPACE, "firstName");
	public final TextPropertyModel lastName = PropertyModels.newText(this, CmModel.NAMESPACE, "lastName");
	public final TextPropertyModel middleName = PropertyModels.newText(this, CmModel.NAMESPACE, "middleName");
	public final TextPropertyModel email = PropertyModels.newText(this, CmModel.NAMESPACE, "email");
	public final TextPropertyModel organizationId = PropertyModels.newText(this, CmModel.NAMESPACE, "organizationId");
	public final TextPropertyModel homeFolderProvider = PropertyModels.newText(this, CmModel.NAMESPACE, "homeFolderProvider");
	public final TextPropertyModel defaultHomeFolderPath = PropertyModels.newText(this, CmModel.NAMESPACE, "defaultHomeFolderPath");
	public final TextPropertyModel presenceProvider = PropertyModels.newText(this, CmModel.NAMESPACE, "presenceProvider");
	public final TextPropertyModel presenceUsername = PropertyModels.newText(this, CmModel.NAMESPACE, "presenceUsername");
	public final TextPropertyModel organization = PropertyModels.newText(this, CmModel.NAMESPACE, "organization");
	public final TextPropertyModel jobtitle = PropertyModels.newText(this, CmModel.NAMESPACE, "jobtitle");
	public final TextPropertyModel location = PropertyModels.newText(this, CmModel.NAMESPACE, "location");
	public final ContentPropertyModel persondescription = PropertyModels.newContent(this, CmModel.NAMESPACE, "persondescription");
	public final TextPropertyModel telephone = PropertyModels.newText(this, CmModel.NAMESPACE, "telephone");
	public final TextPropertyModel mobile = PropertyModels.newText(this, CmModel.NAMESPACE, "mobile");
	public final TextPropertyModel companyaddress1 = PropertyModels.newText(this, CmModel.NAMESPACE, "companyaddress1");
	public final TextPropertyModel companyaddress2 = PropertyModels.newText(this, CmModel.NAMESPACE, "companyaddress2");
	public final TextPropertyModel companyaddress3 = PropertyModels.newText(this, CmModel.NAMESPACE, "companyaddress3");
	public final TextPropertyModel companypostcode = PropertyModels.newText(this, CmModel.NAMESPACE, "companypostcode");
	public final TextPropertyModel companytelephone = PropertyModels.newText(this, CmModel.NAMESPACE, "companytelephone");
	public final TextPropertyModel companyfax = PropertyModels.newText(this, CmModel.NAMESPACE, "companyfax");
	public final TextPropertyModel companyemail = PropertyModels.newText(this, CmModel.NAMESPACE, "companyemail");
	public final TextPropertyModel skype = PropertyModels.newText(this, CmModel.NAMESPACE, "skype");
	public final TextPropertyModel instantmsg = PropertyModels.newText(this, CmModel.NAMESPACE, "instantmsg");
	public final TextPropertyModel userStatus = PropertyModels.newText(this, CmModel.NAMESPACE, "userStatus");
	public final DateTimePropertyModel userStatusTime = PropertyModels.newDateTime(this, CmModel.NAMESPACE, "userStatusTime");
	public final TextPropertyModel googleusername = PropertyModels.newText(this, CmModel.NAMESPACE, "googleusername");
	public final BooleanPropertyModel emailFeedDisabled = PropertyModels.newBoolean(this, CmModel.NAMESPACE, "emailFeedDisabled");
	public final BooleanPropertyModel subscriptionsPrivate = PropertyModels.newBoolean(this, CmModel.NAMESPACE, "subscriptionsPrivate");
	public final LongPropertyModel emailFeedId = PropertyModels.newLong(this, CmModel.NAMESPACE, "emailFeedId",
			ProtectedPropertyConstraint.INSTANCE);
	public final LongPropertyModel sizeCurrent = PropertyModels.newLong(this, CmModel.NAMESPACE, "sizeCurrent",
			ProtectedPropertyConstraint.INSTANCE);
	public final LongPropertyModel sizeQuota = PropertyModels.newLong(this, CmModel.NAMESPACE, "sizeQuota",
			ProtectedPropertyConstraint.INSTANCE);

	public final AssociationModel avatar = new AssociationModel(NameReference.create(CmModel.NAMESPACE, "avatar"));
}
