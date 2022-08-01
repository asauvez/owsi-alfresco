package fr.openwide.alfresco.component.model.repository.model.usr;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.association.ManyToManyAssociationModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiTextPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.component.model.repository.model.UsrModel;

public class UsrAuthorityContainer extends UsrAuthority {

	public UsrAuthorityContainer() {
		super(UsrModel.NAMESPACE.createQName("authorityContainer"));
	}

	protected UsrAuthorityContainer(QName qName) {
		super(qName);
	}
	
	public final TextPropertyModel authorityName = PropertyModels.newText(this, CmModel.NAMESPACE, "authorityName");
	public final MultiTextPropertyModel members = PropertyModels.newMultiText(this, CmModel.NAMESPACE, "members");

	public final ManyToManyAssociationModel member = new ManyToManyAssociationModel(CmModel.NAMESPACE.createQName("member"));
}
