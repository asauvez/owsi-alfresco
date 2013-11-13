package fr.openwide.alfresco.query.web.form.projection.node;

import fr.openwide.alfresco.query.core.node.model.TypeModel;
import fr.openwide.alfresco.query.core.search.model.NodeFetchDetails;
import fr.openwide.alfresco.query.core.search.model.NodeResult;
import fr.openwide.alfresco.query.web.form.projection.Projection;
import fr.openwide.alfresco.query.web.form.projection.ProjectionBuilder;

public class NodeTypeProjection extends Projection<TypeModel> {

	public NodeTypeProjection(ProjectionBuilder builder) {
		super(builder, TypeModel.class);
	}

	@Override
	public TypeModel apply(NodeResult result) {
		return result.getType();
	}
	
	@Override
	public String getDefaultLabelCode() {
		return "typeprojection.label";
	}
	
	@Override
	public void initNodeFetchDetails(NodeFetchDetails nodeFetchDetails) {
		super.initNodeFetchDetails(nodeFetchDetails);
		nodeFetchDetails.setTypeFect(true);
	}
}
