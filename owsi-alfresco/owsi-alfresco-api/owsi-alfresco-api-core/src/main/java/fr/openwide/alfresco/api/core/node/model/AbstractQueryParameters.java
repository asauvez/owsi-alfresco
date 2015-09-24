package fr.openwide.alfresco.api.core.node.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fr.openwide.alfresco.api.core.search.model.RepositorySortDefinition;

public abstract class AbstractQueryParameters implements Serializable {

	private NodeScope nodeScope = new NodeScope();
	private List<RepositorySortDefinition> sorts = new ArrayList<>();

	public NodeScope getNodeScope() {
		return nodeScope;
	}
	public void setNodeScope(NodeScope nodeScope) {
		this.nodeScope = nodeScope;
	}

	public List<RepositorySortDefinition> getSorts() {
		return sorts;
	}
}
