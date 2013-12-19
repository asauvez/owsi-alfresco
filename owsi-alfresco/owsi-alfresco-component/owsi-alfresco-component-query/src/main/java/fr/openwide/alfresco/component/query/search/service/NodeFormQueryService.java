package fr.openwide.alfresco.component.query.search.service;

import java.util.List;

import fr.openwide.alfresco.component.model.node.model.AssociationModel;
import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.query.form.result.FormQueryResult;
import fr.openwide.alfresco.component.query.search.model.NodeFormQuery;
import fr.openwide.alfresco.component.query.search.model.SearchFormQuery;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

public interface NodeFormQueryService {

	FormQueryResult<RepositoryNode> list(NodeFormQuery formQuery, List<RepositoryNode> list);

	FormQueryResult<RepositoryNode> search(SearchFormQuery formQuery);

	FormQueryResult<RepositoryNode>	getChildren(NodeFormQuery formQuery, NodeReference parent);
	
	FormQueryResult<RepositoryNode>	getChildren(NodeFormQuery formQuery, NodeReference parent, ChildAssociationModel childAssoc);

	FormQueryResult<RepositoryNode> getTargetAssocs(NodeFormQuery formQuery, NodeReference parent, AssociationModel assoc);
	
	FormQueryResult<RepositoryNode> getSourceAssocs(NodeFormQuery formQuery, NodeReference parent, AssociationModel assoc);

}
