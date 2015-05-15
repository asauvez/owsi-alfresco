package fr.openwide.alfresco.component.query.form.projection.node;

import fr.openwide.alfresco.component.query.form.projection.ProjectionImpl;
import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;

public abstract class AbstractNodeProjectionImpl<P> extends ProjectionImpl<RepositoryNode, NodeProjectionBuilder, P>
	implements NodeScopeInitializer {

	public AbstractNodeProjectionImpl(NodeProjectionBuilder builder, Class<? super P> mappedClass) {
		super(builder, mappedClass);
	}

	@Override
	public void initNodeScope(NodeScope nodeScope) {
		// to override
	}

}
