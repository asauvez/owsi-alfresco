package fr.openwide.alfresco.component.query.form.projection.node;

import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;

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
	public void initNodeScope(NodeScope nodeScope) {
		super.initNodeScope(nodeScope);
		
		nodeScope.setNodeReference(true);
	}
}
