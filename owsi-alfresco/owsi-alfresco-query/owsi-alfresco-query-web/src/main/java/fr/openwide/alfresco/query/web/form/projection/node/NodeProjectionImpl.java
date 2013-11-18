package fr.openwide.alfresco.query.web.form.projection.node;

import fr.openwide.alfresco.query.api.search.model.NodeFetchDetails;
import fr.openwide.alfresco.query.api.search.model.NodeResult;
import fr.openwide.alfresco.query.web.form.projection.ProjectionImpl;

public abstract class NodeProjectionImpl<P> extends ProjectionImpl<NodeResult, NodeProjectionBuilder, P>
	implements NodeFetchDetailsInitializer {

	public NodeProjectionImpl(NodeProjectionBuilder builder, Class<P> mappedClass) {
		super(builder, mappedClass);
	}

	@Override
	public void initNodeFetchDetails(NodeFetchDetails nodeFetchDetails) {
		// to override
	}

}
