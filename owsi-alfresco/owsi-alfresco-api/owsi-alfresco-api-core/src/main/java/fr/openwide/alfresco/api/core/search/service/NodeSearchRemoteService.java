package fr.openwide.alfresco.api.core.search.service;

import java.util.List;

import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.search.model.RepositorySearchParameters;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.WebScriptMethod;
import fr.openwide.alfresco.repo.wsgenerator.annotation.WebScriptEndPoint;
import fr.openwide.alfresco.repo.wsgenerator.model.WebScriptParam;

public interface NodeSearchRemoteService {

	@WebScriptEndPoint(method=WebScriptMethod.POST, url="/owsi/search/node/search")
	class SEARCH_NODE_SERVICE extends WebScriptParam<List<RepositoryNode>> {
		public RepositorySearchParameters searchParameters;
		public NodeScope nodeScope;
	}
	List<RepositoryNode> search(RepositorySearchParameters searchParameters, NodeScope nodeScope);

}
