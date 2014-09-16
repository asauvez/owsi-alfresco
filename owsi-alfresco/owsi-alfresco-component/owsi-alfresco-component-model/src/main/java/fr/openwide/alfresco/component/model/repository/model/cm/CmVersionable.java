package fr.openwide.alfresco.component.model.repository.model.cm;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.BooleanPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class CmVersionable extends AspectModel {

	public CmVersionable() {
		super(NameReference.create(CmModel.NAMESPACE, "versionable"));
	}

	protected CmVersionable(NameReference nameReference) {
		super(nameReference);
	}

	public final TextPropertyModel versionLabel = PropertyModels.newText(this, "versionLabel");

	public final TextPropertyModel versionType = PropertyModels.newText(this, "versionType");

	public final BooleanPropertyModel initialVersion = PropertyModels.newBoolean(this, "initialVersion");

	public final BooleanPropertyModel autoVersion = PropertyModels.newBoolean(this, "autoVersion");

	public final BooleanPropertyModel autoVersionOnUpdateProps = PropertyModels.newBoolean(this, "autoVersionOnUpdateProps");

}
