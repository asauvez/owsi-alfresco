package fr.openwide.alfresco.component.model.repository.model.cm;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.node.model.constraint.AuthorityNamePropertyConstraint;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmAuthorityContainer extends CmAuthority {

	public CmAuthorityContainer() {
		super(CmModel.NAMESPACE.createQName("authorityContainer"));
	}

	protected CmAuthorityContainer(QName qName) {
		super(qName);
	}

	public final TextPropertyModel authorityName = PropertyModels.newText(this, CmModel.NAMESPACE, "authorityName",
			AuthorityNamePropertyConstraint.INSTANCE);
	
	public final TextPropertyModel authorityDisplayName = PropertyModels.newText(this, CmModel.NAMESPACE, "authorityDisplayName");
	
	public final ChildAssociationModel member = new ChildAssociationModel(CmModel.NAMESPACE.createQName("member"));
}
