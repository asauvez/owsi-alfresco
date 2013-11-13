package fr.openwide.alfresco.query.web.search.service;

import java.util.List;

import fr.openwide.alfresco.query.core.node.model.value.NameReference;
import fr.openwide.alfresco.query.core.node.model.value.NodeReference;
import fr.openwide.alfresco.query.core.search.model.NodeResult;
import fr.openwide.alfresco.query.web.form.result.FormQueryResult;
import fr.openwide.alfresco.query.web.search.model.NodeFormQuery;
import fr.openwide.alfresco.query.web.search.model.SearchFormQuery;

public interface NodeFormQueryService {
	
	FormQueryResult<NodeResult> list(NodeFormQuery formQuery, List<NodeResult> list);

	FormQueryResult<NodeResult> search(SearchFormQuery formQuery);

	FormQueryResult<NodeResult>	children(NodeFormQuery formQuery, NodeReference parent, NameReference nameReference);

}
