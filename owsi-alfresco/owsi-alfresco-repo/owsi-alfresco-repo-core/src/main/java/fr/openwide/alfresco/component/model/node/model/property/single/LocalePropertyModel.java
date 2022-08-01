package fr.openwide.alfresco.component.model.node.model.property.single;

import java.util.Locale;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;

public class LocalePropertyModel extends SinglePropertyModel<Locale> {

	public LocalePropertyModel(ContainerModel type, QName qName) {
		super(type, qName);
	}

	@Override
	public Class<Locale> getValueClass() {
		return Locale.class;
	}

	@Override
	public String getDataType() {
		return "d:category";
	}
}
