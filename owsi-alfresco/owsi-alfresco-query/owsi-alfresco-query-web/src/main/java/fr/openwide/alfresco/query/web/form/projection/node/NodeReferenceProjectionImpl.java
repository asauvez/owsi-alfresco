package fr.openwide.alfresco.query.web.form.projection.node;

import fr.openwide.alfresco.query.api.node.model.NodeReference;
import fr.openwide.alfresco.query.api.search.model.NodeFetchDetails;
import fr.openwide.alfresco.query.api.search.model.NodeResult;

public class NodeReferenceProjectionImpl extends NodeProjectionImpl<NodeReference> {

	public NodeReferenceProjectionImpl(NodeProjectionBuilder builder) {
		super(builder, NodeReference.class);
	}

	@Override
	public NodeReference apply(NodeResult result) {
		return result.getNodeReference();
	}

	@Override
	public String getDefaultLabelCode() {
		return "refprojection.label";
	}
	
	@Override
	public void initNodeFetchDetails(NodeFetchDetails nodeFetchDetails) {
		super.initNodeFetchDetails(nodeFetchDetails);
		
		nodeFetchDetails.setNodeReference(true);
	}
}
