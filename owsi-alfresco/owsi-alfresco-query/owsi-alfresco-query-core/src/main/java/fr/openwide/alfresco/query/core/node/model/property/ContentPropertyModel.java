package fr.openwide.alfresco.query.core.node.model.property;

import fr.openwide.alfresco.query.core.node.model.ContainerModel;
import fr.openwide.alfresco.repository.api.node.model.RepositoryContentData;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class ContentPropertyModel extends PropertyModel<RepositoryContentData> {

	public ContentPropertyModel(ContainerModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public Class<RepositoryContentData> getValueClass() {
		return RepositoryContentData.class;
	}

}
