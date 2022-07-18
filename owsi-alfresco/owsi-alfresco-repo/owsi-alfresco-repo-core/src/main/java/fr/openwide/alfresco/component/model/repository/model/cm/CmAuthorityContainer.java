package fr.openwide.alfresco.component.model.repository.model.cm;

import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.node.model.constraint.AuthorityNamePropertyConstraint;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class CmAuthorityContainer extends CmAuthority {

	public CmAuthorityContainer() {
		super(NameReference.create(CmModel.NAMESPACE, "authorityContainer"));
	}

	protected CmAuthorityContainer(NameReference nameReference) {
		super(nameReference);
	}

	public final TextPropertyModel authorityName = PropertyModels.newText(this, CmModel.NAMESPACE, "authorityName",
			AuthorityNamePropertyConstraint.INSTANCE);
	
	public final TextPropertyModel authorityDisplayName = PropertyModels.newText(this, CmModel.NAMESPACE, "authorityDisplayName");
	
	public final ChildAssociationModel member = new ChildAssociationModel(NameReference.create(CmModel.NAMESPACE, "member"));
}
