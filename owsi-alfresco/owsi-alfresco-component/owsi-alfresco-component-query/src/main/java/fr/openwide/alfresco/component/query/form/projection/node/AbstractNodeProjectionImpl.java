package fr.openwide.alfresco.component.query.form.projection.node;

import fr.openwide.alfresco.component.query.form.projection.ProjectionImpl;
import fr.openwide.alfresco.repository.api.node.model.NodeFetchDetails;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;

public abstract class AbstractNodeProjectionImpl<P> extends ProjectionImpl<RepositoryNode, NodeProjectionBuilder, P>
	implements NodeFetchDetailsInitializer {

	public AbstractNodeProjectionImpl(NodeProjectionBuilder builder, Class<? super P> mappedClass) {
		super(builder, mappedClass);
	}

	@Override
	public void initNodeFetchDetails(NodeFetchDetails nodeFetchDetails) {
		// to override
	}

}
