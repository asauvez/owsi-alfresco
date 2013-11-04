package fr.openwide.alfresco.query.core.search.projection;

import fr.openwide.alfresco.query.core.node.model.TypeModel;
import fr.openwide.alfresco.query.core.search.model.NodeResult;

public class TypeProjection extends Projection<TypeModel> {

	public TypeProjection(ProjectionBuilder builder) {
		super(builder);
	}

	@Override
	public TypeModel getValue(NodeResult result) {
		return result.getType();
	}
}
