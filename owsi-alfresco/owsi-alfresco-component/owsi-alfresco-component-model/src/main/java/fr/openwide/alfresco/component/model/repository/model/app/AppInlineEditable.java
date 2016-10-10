package fr.openwide.alfresco.component.model.repository.model.app;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.BooleanPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.AppModel;

public class AppInlineEditable extends AspectModel {
	
	public AppInlineEditable() {
		super(NameReference.create(AppModel.NAMESPACE, "inlineeditable"));
	}

	protected AppInlineEditable(NameReference nameReference) {
		super(nameReference);
	}

	public final BooleanPropertyModel editInline = PropertyModels.newBoolean(this, AppModel.NAMESPACE, "editInline");
}
