package fr.openwide.alfresco.component.model.repository.model.cm;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmSummarizable extends AspectModel {

	public CmSummarizable() {
		super(NameReference.create(CmModel.NAMESPACE, "summarizable"));
	}

	protected CmSummarizable(NameReference nameReference) {
		super(nameReference);
	}

	public final TextPropertyModel summary = PropertyModels.newText(this, CmModel.NAMESPACE, "summary");

}
