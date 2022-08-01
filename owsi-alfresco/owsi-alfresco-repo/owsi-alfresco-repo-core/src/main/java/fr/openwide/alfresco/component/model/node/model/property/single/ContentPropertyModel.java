package fr.openwide.alfresco.component.model.node.model.property.single;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.api.core.node.model.RepositoryContentData;
import fr.openwide.alfresco.component.model.node.model.ContainerModel;

public class ContentPropertyModel extends SinglePropertyModel<RepositoryContentData> {

	public ContentPropertyModel(ContainerModel type, QName qName) {
		super(type, qName);
	}

	@Override
	public Class<RepositoryContentData> getValueClass() {
		return RepositoryContentData.class;
	}

	@Override
	public String getDataType() {
		return "d:content";
	}
}
