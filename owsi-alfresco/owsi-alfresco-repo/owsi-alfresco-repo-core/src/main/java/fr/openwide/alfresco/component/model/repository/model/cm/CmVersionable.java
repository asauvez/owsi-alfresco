package fr.openwide.alfresco.component.model.repository.model.cm;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.constraint.ProtectedPropertyConstraint;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.BooleanPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmVersionable extends AspectModel {

	public CmVersionable() {
		super(CmModel.NAMESPACE.createQName("versionable"));
	}

	protected CmVersionable(QName qName) {
		super(qName);
	}

	public final TextPropertyModel versionLabel = PropertyModels.newText(this, CmModel.NAMESPACE, "versionLabel",
			ProtectedPropertyConstraint.INSTANCE);

	public final TextPropertyModel versionType = PropertyModels.newText(this, CmModel.NAMESPACE, "versionType",
			ProtectedPropertyConstraint.INSTANCE);

	public final BooleanPropertyModel initialVersion = PropertyModels.newBoolean(this, CmModel.NAMESPACE, "initialVersion");

	public final BooleanPropertyModel autoVersion = PropertyModels.newBoolean(this, CmModel.NAMESPACE, "autoVersion");

	public final BooleanPropertyModel autoVersionOnUpdateProps = PropertyModels.newBoolean(this, CmModel.NAMESPACE, "autoVersionOnUpdateProps");

}
