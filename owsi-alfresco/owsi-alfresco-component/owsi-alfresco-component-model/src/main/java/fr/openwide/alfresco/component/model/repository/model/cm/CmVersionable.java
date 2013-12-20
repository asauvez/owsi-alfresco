package fr.openwide.alfresco.component.model.repository.model.cm;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.BooleanPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class CmVersionable extends AspectModel {

	public CmVersionable() {
		super(NameReference.create(CmModel.NAMESPACE, "versionable"));
	}

	protected CmVersionable(NameReference nameReference) {
		super(nameReference);
	}

	public final TextPropertyModel versionLabel = PropertyModels.newText(this, CmModel.NAMESPACE, "versionLabel");

	public final TextPropertyModel versionType = PropertyModels.newText(this, CmModel.NAMESPACE, "versionType");

	public final BooleanPropertyModel initialVersion = PropertyModels.newBoolean(this, CmModel.NAMESPACE, "initialVersion");

	public final BooleanPropertyModel autoVersion = PropertyModels.newBoolean(this, CmModel.NAMESPACE, "autoVersion");

	public final BooleanPropertyModel autoVersionOnUpdateProps = PropertyModels.newBoolean(this, CmModel.NAMESPACE, "autoVersionOnUpdateProps");

}
