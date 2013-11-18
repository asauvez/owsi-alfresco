package fr.openwide.alfresco.query.web.form.projection.node;

import fr.openwide.alfresco.query.api.node.model.NameReference;
import fr.openwide.alfresco.query.api.search.model.NodeFetchDetails;
import fr.openwide.alfresco.query.api.search.model.NodeResult;

public class NodeTypeProjectionImpl extends NodeProjectionImpl<NameReference> {

	public NodeTypeProjectionImpl(NodeProjectionBuilder builder) {
		super(builder, NameReference.class);
	}

	@Override
	public NameReference apply(NodeResult result) {
		return result.getType();
	}

	@Override
	public String getDefaultLabelCode() {
		return "typeprojection.label";
	}

	@Override
	public void initNodeFetchDetails(NodeFetchDetails nodeFetchDetails) {
		super.initNodeFetchDetails(nodeFetchDetails);
		
		nodeFetchDetails.setType(true);
	}
}
