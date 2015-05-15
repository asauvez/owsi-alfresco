package fr.openwide.alfresco.component.query.form.projection.node;

import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

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
	public void initNodeScope(NodeScope nodeScope) {
		super.initNodeScope(nodeScope);
		
		nodeScope.setType(true);
	}
}
