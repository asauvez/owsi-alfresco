package fr.openwide.alfresco.component.model.repository.model.st;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.repository.model.StModel;
import fr.openwide.alfresco.component.model.repository.model.cm.CmFolder;

public class StSites extends CmFolder {
	
	public StSites() {
		super(StModel.NAMESPACE.createQName("sites"));
	}

	protected StSites(QName qName) {
		super(qName);
	}

	/*
	public final TextPropertyModel name = PropertyModels.newText(this, StModel.NAMESPACE, "name",
			MandatoryEnforcedPropertyConstraint.INSTANCE,
			new RegexPropertyConstraint("(.*[\\\"\\*\\\\\\>\\<\\?\\/\\:\\|]+.*)|(.*[\\.]?.*[\\.]+$)|(.*[ ]+$)", false));

	public final CmAuditable auditable = addMandatoryAspect(CmModel.auditable);*/
}
