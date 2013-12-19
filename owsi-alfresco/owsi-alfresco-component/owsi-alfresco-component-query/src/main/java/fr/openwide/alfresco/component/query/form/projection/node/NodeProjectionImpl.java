package fr.openwide.alfresco.component.query.form.projection.node;

import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;

public class NodeProjectionImpl extends AbstractNodeProjectionImpl<RepositoryNode> {

	public NodeProjectionImpl(NodeProjectionBuilder builder) {
		super(builder, RepositoryNode.class);
	}

	@Override
	public RepositoryNode apply(RepositoryNode result) {
		return result;
	}

	@Override
	public String getDefaultLabelCode() {
		return "nodeprojection.label";
	}
}
