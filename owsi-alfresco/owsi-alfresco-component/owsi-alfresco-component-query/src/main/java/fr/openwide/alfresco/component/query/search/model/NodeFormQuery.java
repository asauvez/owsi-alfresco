package fr.openwide.alfresco.component.query.search.model;

import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.component.query.form.projection.node.NodeProjectionBuilder;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.model.StoreReference;

public abstract class NodeFormQuery extends AbstractFormQuery<RepositoryNode> {

	public void initNodeProjections(NodeProjectionBuilder builder) {
		// to override
		builder
			.ref().of()
			.type().of()
			.prop(CmModel.object.name).of();
	}

	public StoreReference getStoreReference() {
		return StoreReference.STORE_REF_WORKSPACE_SPACESSTORE;
	}
}
