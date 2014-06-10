package fr.openwide.alfresco.component.query.form.projection.node;

import fr.openwide.alfresco.repository.api.node.model.NodeScope;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;

public class NodePathProjectionImpl extends AbstractNodeProjectionImpl<String> {

	public NodePathProjectionImpl(NodeProjectionBuilder builder) {
		super(builder, String.class);
	}

	@Override
	public String apply(RepositoryNode result) {
		return result.getPath();
	}

	@Override
	public String getDefaultLabelCode() {
		return "pathprojection.label";
	}

	@Override
	public void initNodeScope(NodeScope nodeScope) {
		super.initNodeScope(nodeScope);
		
		nodeScope.setPath(true);
	}
}
