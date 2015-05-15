package fr.openwide.alfresco.component.model.node.model.property.single;

import java.util.Locale;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class LocalePropertyModel extends SinglePropertyModel<Locale> {

	public LocalePropertyModel(ContainerModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public Class<Locale> getValueClass() {
		return Locale.class;
	}

}
