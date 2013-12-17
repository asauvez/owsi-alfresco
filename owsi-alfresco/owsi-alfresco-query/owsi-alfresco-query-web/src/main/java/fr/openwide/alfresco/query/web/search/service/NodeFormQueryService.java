package fr.openwide.alfresco.query.web.search.service;

import java.util.List;

import fr.openwide.alfresco.query.web.form.result.FormQueryResult;
import fr.openwide.alfresco.query.web.search.model.NodeFormQuery;
import fr.openwide.alfresco.query.web.search.model.SearchFormQuery;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

public interface NodeFormQueryService {

	FormQueryResult<RepositoryNode> list(NodeFormQuery formQuery, List<RepositoryNode> list);

	FormQueryResult<RepositoryNode> search(SearchFormQuery formQuery);

	FormQueryResult<RepositoryNode>	getChildren(NodeFormQuery formQuery, NodeReference parent, NameReference childAssocName);

	FormQueryResult<RepositoryNode> getTargetAssocs(NodeFormQuery formQuery, NodeReference parent, NameReference assocName);
	
	FormQueryResult<RepositoryNode> getSourceAssocs(NodeFormQuery formQuery, NodeReference parent, NameReference assocName);

}
