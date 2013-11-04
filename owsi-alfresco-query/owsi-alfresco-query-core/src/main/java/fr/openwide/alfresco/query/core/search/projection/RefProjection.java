package fr.openwide.alfresco.query.core.search.projection;

import fr.openwide.alfresco.query.core.node.model.value.NodeReference;
import fr.openwide.alfresco.query.core.search.model.NodeResult;

public class RefProjection extends Projection<NodeReference> {

	public RefProjection(ProjectionBuilder builder) {
		super(builder);
	}

	@Override
	public NodeReference getValue(NodeResult result) {
		return result.getReference();
	}

}
