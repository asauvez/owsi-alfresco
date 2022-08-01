package fr.openwide.alfresco.component.model.repository.model.app;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.BooleanPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.AppModel;

public class AppInlineEditable extends AspectModel {
	
	public AppInlineEditable() {
		super(AppModel.NAMESPACE.createQName("inlineeditable"));
	}

	protected AppInlineEditable(QName qName) {
		super(qName);
	}

	public final BooleanPropertyModel editInline = PropertyModels.newBoolean(this, AppModel.NAMESPACE, "editInline");
}
