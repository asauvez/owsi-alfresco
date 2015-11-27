package fr.openwide.alfresco.component.model.node.model.embed;

import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.app.core.node.model.RenditionsSetter;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;

public class RenditionsNodeScope extends RenditionsSetter<NodeScopeBuilder> {

	private final NodeScope scope;
	
	public RenditionsNodeScope(NodeScopeBuilder builder) {
		this.scope = builder.getScope();
	}
	
	@Override
	public NodeScopeBuilder name(NameReference renditionName) {
		NodeScopeBuilder renditionNodeScope = new NodeScopeBuilder();
		this.scope.getRenditions().put(renditionName, renditionNodeScope.getScope());
		return renditionNodeScope;
	}
}
