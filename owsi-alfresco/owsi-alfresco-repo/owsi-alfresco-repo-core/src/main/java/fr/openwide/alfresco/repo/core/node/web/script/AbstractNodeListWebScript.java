package fr.openwide.alfresco.repo.core.node.web.script;

import java.util.Collection;
import java.util.List;

import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.repo.wsgenerator.model.WebScriptParam;

public abstract class AbstractNodeListWebScript<P extends WebScriptParam<List<RepositoryNode>>> 
		extends AbstractNodeWebScript<List<RepositoryNode>, P> {

	@Override
	protected Collection<RepositoryNode> getOutputNodes(List<RepositoryNode> result) {
		return result;
	}

}