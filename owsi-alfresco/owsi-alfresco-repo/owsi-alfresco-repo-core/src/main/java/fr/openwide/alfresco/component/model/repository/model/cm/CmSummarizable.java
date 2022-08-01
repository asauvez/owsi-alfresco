package fr.openwide.alfresco.component.model.repository.model.cm;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmSummarizable extends AspectModel {

	public CmSummarizable() {
		super(CmModel.NAMESPACE.createQName("summarizable"));
	}

	protected CmSummarizable(QName qName) {
		super(qName);
	}

	public final TextPropertyModel summary = PropertyModels.newText(this, CmModel.NAMESPACE, "summary");

}
