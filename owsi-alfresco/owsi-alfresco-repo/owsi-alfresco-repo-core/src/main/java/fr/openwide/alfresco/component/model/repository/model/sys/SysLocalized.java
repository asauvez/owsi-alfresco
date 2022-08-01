package fr.openwide.alfresco.component.model.repository.model.sys;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.LocalePropertyModel;
import fr.openwide.alfresco.component.model.repository.model.SysModel;

public class SysLocalized extends AspectModel {

	public SysLocalized() {
		super(SysModel.NAMESPACE.createQName("localized"));
	}

	protected SysLocalized(QName qName) {
		super(qName);
	}

	public final LocalePropertyModel locale = PropertyModels.newLocale(this, SysModel.NAMESPACE, "locale");
	
}
