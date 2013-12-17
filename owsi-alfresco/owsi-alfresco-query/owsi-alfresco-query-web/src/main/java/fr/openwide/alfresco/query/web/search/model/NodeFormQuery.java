package fr.openwide.alfresco.query.web.search.model;

import fr.openwide.alfresco.query.core.repository.model.CmModel;
import fr.openwide.alfresco.query.web.form.projection.node.NodeProjectionBuilder;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;

public abstract class NodeFormQuery extends AbstractFormQuery<RepositoryNode> {

	public void initNodeProjections(NodeProjectionBuilder builder) {
		// to override
		builder
			.ref().of()
			.type().of()
			.prop(CmModel.object.name).of();
	}

}
