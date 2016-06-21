package fr.openwide.alfresco.component.model.repository.model.st;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.repository.model.StModel;
import fr.openwide.alfresco.component.model.repository.model.cm.CmFolder;

public class StSites extends CmFolder {
	
	public StSites() {
		super(NameReference.create(StModel.NAMESPACE, "sites"));
	}

	protected StSites(NameReference nameReference) {
		super(nameReference);
	}

	/*
	public final TextPropertyModel name = PropertyModels.newText(this, StModel.NAMESPACE, "name",
			MandatoryEnforcedPropertyConstraint.INSTANCE,
			new RegexPropertyConstraint("(.*[\\\"\\*\\\\\\>\\<\\?\\/\\:\\|]+.*)|(.*[\\.]?.*[\\.]+$)|(.*[ ]+$)", false));

	public final CmAuditable auditable = addMandatoryAspect(CmModel.auditable);*/
}
