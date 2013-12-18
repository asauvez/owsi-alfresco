package fr.openwide.alfresco.query.web.form.projection.node;

import fr.openwide.alfresco.repository.api.node.model.NodeFetchDetails;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

public class NodeReferenceProjectionImpl extends AbstractNodeProjectionImpl<NodeReference> {

	public NodeReferenceProjectionImpl(NodeProjectionBuilder builder) {
		super(builder, NodeReference.class);
	}

	@Override
	public NodeReference apply(RepositoryNode result) {
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
