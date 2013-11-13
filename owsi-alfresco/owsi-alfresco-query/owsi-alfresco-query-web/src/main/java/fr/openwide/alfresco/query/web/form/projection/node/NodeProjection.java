package fr.openwide.alfresco.query.web.form.projection.node;

import fr.openwide.alfresco.query.core.search.model.NodeResult;
import fr.openwide.alfresco.query.web.form.projection.Projection;
import fr.openwide.alfresco.query.web.form.projection.ProjectionBuilder;

public class NodeProjection extends Projection<NodeResult> {

	public NodeProjection(ProjectionBuilder builder) {
		super(builder, NodeResult.class);
	}

	@Override
	public NodeResult apply(NodeResult result) {
		return result;
	}

	@Override
	public String getDefaultLabelCode() {
		return "nodeprojection.label";
	}
}
