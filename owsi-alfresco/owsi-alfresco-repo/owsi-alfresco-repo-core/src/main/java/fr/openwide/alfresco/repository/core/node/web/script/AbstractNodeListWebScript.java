package fr.openwide.alfresco.repository.core.node.web.script;

import java.util.Collection;
import java.util.List;

import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;

public abstract class AbstractNodeListWebScript<P> extends AbstractNodeWebScript<List<RepositoryNode>, P> {

	@Override
	protected Collection<RepositoryNode> getDownloadedNodes(List<RepositoryNode> response) {
		return response;
	}
	
}