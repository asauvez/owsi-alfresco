package fr.openwide.alfresco.component.model.repository.model.cm;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.BooleanPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmIndexControl extends AspectModel {

	public CmIndexControl() {
		super(CmModel.NAMESPACE.createQName("indexControl"));
	}

	protected CmIndexControl(QName qName) {
		super(qName);
	}

	public final BooleanPropertyModel isIndexed = PropertyModels.newBoolean(this, CmModel.NAMESPACE, "isIndexed");

	public final BooleanPropertyModel isContentIndexed = PropertyModels.newBoolean(this, CmModel.NAMESPACE, "isContentIndexed");

}
