package fr.openwide.alfresco.component.model.repository.model.dl;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.TypeModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.DlModel;

public class DlDataList extends TypeModel {

	public DlDataList(NameReference nameReference) {
		super(nameReference);
	}
	
	public final TextPropertyModel dataListItemType = PropertyModels.newText(this, DlModel.NAMESPACE, "dataListItemType");
}
