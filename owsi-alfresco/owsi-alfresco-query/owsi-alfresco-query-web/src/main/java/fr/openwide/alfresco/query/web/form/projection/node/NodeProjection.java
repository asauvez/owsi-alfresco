package fr.openwide.alfresco.query.web.form.projection.node;

import fr.openwide.alfresco.query.core.search.model.NodeFetchDetails;
import fr.openwide.alfresco.query.core.search.model.NodeResult;
import fr.openwide.alfresco.query.web.form.projection.ProjectionImpl;

public abstract class NodeProjection<P> extends ProjectionImpl<NodeResult, NodeProjectionBuilder, P> {

	public NodeProjection(NodeProjectionBuilder builder, Class<P> mappedClass) {
		super(builder, mappedClass);
	}

	public void initNodeFetchDetails(@SuppressWarnings("unused") NodeFetchDetails nodeFetchDetails) {
		// to override
	}

}
