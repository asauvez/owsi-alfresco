package fr.openwide.alfresco.component.model.node.model.property.single;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.api.core.node.model.RepositoryContentData;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class ContentPropertyModel extends SinglePropertyModel<RepositoryContentData> {

	public ContentPropertyModel(ContainerModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public Class<RepositoryContentData> getValueClass() {
		return RepositoryContentData.class;
	}

}
