package fr.openwide.alfresco.component.model.repository.model.st;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.repository.model.StModel;

public class StCustomSiteProperties extends AspectModel {
	
	public StCustomSiteProperties() {
		super(NameReference.create(StModel.NAMESPACE, "customsiteproperties"));
	}

	protected StCustomSiteProperties(NameReference nameReference) {
		super(nameReference);
	}

	/*
	public final TextPropertyModel name = PropertyModels.newText(this, StModel.NAMESPACE, "name",
			MandatoryEnforcedPropertyConstraint.INSTANCE,
			new RegexPropertyConstraint("(.*[\\\"\\*\\\\\\>\\<\\?\\/\\:\\|]+.*)|(.*[\\.]?.*[\\.]+$)|(.*[ ]+$)", false));

	public final CmAuditable auditable = addMandatoryAspect(CmModel.auditable);*/
}
