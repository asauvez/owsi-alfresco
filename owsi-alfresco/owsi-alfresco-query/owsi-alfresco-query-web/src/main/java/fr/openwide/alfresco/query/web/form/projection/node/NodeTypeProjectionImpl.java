package fr.openwide.alfresco.query.web.form.projection.node;

import fr.openwide.alfresco.repository.api.node.model.NodeFetchDetails;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class NodeTypeProjectionImpl extends AbstractNodeProjectionImpl<NameReference> {

	public NodeTypeProjectionImpl(NodeProjectionBuilder builder) {
		super(builder, NameReference.class);
	}

	@Override
	public NameReference apply(RepositoryNode result) {
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
