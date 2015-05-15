package fr.openwide.alfresco.component.model.repository.model.sys;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.LocalePropertyModel;
import fr.openwide.alfresco.component.model.repository.model.SysModel;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class SysLocalized extends AspectModel {

	public SysLocalized() {
		super(NameReference.create(SysModel.NAMESPACE, "localized"));
	}

	protected SysLocalized(NameReference nameReference) {
		super(nameReference);
	}

	public final LocalePropertyModel locale = PropertyModels.newLocale(this, SysModel.NAMESPACE, "locale");
	
}
