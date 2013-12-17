package fr.openwide.alfresco.query.web.form.projection.node;

import fr.openwide.alfresco.query.web.form.projection.ProjectionImpl;
import fr.openwide.alfresco.repository.api.node.model.NodeFetchDetails;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;

public abstract class NodeProjectionImpl<P> extends ProjectionImpl<RepositoryNode, NodeProjectionBuilder, P>
	implements NodeFetchDetailsInitializer {

	public NodeProjectionImpl(NodeProjectionBuilder builder, Class<P> mappedClass) {
		super(builder, mappedClass);
	}

	@Override
	public void initNodeFetchDetails(NodeFetchDetails nodeFetchDetails) {
		// to override
	}

}
