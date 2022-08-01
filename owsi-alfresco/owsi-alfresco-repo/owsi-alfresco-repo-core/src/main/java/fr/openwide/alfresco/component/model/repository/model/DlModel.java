package fr.openwide.alfresco.component.model.repository.model;

import fr.openwide.alfresco.api.core.remote.model.NamespaceReference;
import fr.openwide.alfresco.component.model.repository.model.dl.DlDataList;
import fr.openwide.alfresco.component.model.repository.model.dl.DlDataListItem;

public interface DlModel {

	NamespaceReference NAMESPACE = NamespaceReference.create("dl", "http://www.alfresco.org/model/datalist/1.0");

	// ---- Aspects

	// ---- Types
	DlDataList dataList = new DlDataList(NAMESPACE.createQName("dataList"));
	DlDataListItem dataListItem = new DlDataListItem(NAMESPACE.createQName("dataListItem"));
}
