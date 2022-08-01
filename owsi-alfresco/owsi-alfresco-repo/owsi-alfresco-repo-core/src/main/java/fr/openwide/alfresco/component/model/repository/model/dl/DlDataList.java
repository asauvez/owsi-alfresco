package fr.openwide.alfresco.component.model.repository.model.dl;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.TypeModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.DlModel;

public class DlDataList extends TypeModel {

	public DlDataList(QName qName) {
		super(qName);
	}
	
	public final TextPropertyModel dataListItemType = PropertyModels.newText(this, DlModel.NAMESPACE, "dataListItemType");
}
