package fr.openwide.alfresco.component.model.repository.model.usr;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.association.ManyToManyAssociationModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiTextPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.component.model.repository.model.UsrModel;

public class UsrAuthorityContainer extends UsrAuthority {

	public UsrAuthorityContainer() {
		super(NameReference.create(UsrModel.NAMESPACE, "authorityContainer"));
	}

	protected UsrAuthorityContainer(NameReference nameReference) {
		super(nameReference);
	}
	
	public final TextPropertyModel authorityName = PropertyModels.newText(this, CmModel.NAMESPACE, "authorityName");
	public final MultiTextPropertyModel members = PropertyModels.newMultiText(this, CmModel.NAMESPACE, "members");

	public final ManyToManyAssociationModel member = new ManyToManyAssociationModel(NameReference.create(CmModel.NAMESPACE, "member"));
}
