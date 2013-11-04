package fr.openwide.alfresco.query.core.search.projection;

import fr.openwide.alfresco.query.core.search.model.NodeResult;

public class NodeProjection extends Projection<NodeResult> {

	public NodeProjection(ProjectionBuilder builder) {
		super(builder);
	}

	@Override
	public NodeResult getValue(NodeResult result) {
		return result;
	}

}
