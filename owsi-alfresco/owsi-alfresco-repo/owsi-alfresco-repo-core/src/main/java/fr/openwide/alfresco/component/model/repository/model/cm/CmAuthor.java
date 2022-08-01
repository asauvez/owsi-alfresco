package fr.openwide.alfresco.component.model.repository.model.cm;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmAuthor extends AspectModel {

	public CmAuthor() {
		super(CmModel.NAMESPACE.createQName("author"));
	}

	protected CmAuthor(QName qName) {
		super(qName);
	}

	public final TextPropertyModel author = PropertyModels.newText(this, CmModel.NAMESPACE, "author");

}
