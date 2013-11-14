package fr.openwide.alfresco.query.web.search.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.openwide.alfresco.query.core.node.model.value.NameReference;
import fr.openwide.alfresco.query.core.node.model.value.NodeReference;
import fr.openwide.alfresco.query.core.search.model.NodeFetchDetails;
import fr.openwide.alfresco.query.core.search.model.NodeResult;
import fr.openwide.alfresco.query.core.search.restriction.RestrictionBuilder;
import fr.openwide.alfresco.query.core.search.restriction.RestrictionBuilder.LogicalOperator;
import fr.openwide.alfresco.query.core.search.service.NodeSearchService;
import fr.openwide.alfresco.query.web.form.projection.Projection;
import fr.openwide.alfresco.query.web.form.projection.node.NodeProjection;
import fr.openwide.alfresco.query.web.form.projection.node.NodeProjectionBuilder;
import fr.openwide.alfresco.query.web.form.result.FormQueryResult;
import fr.openwide.alfresco.query.web.search.model.NodeFormQuery;
import fr.openwide.alfresco.query.web.search.model.SearchFormQuery;
import fr.openwide.alfresco.query.web.search.service.NodeFormQueryService;

@Service
public class NodeFormQueryServiceImpl extends AbstractFormQueryService implements NodeFormQueryService {
	
	@Autowired private NodeSearchService nodeSearchService;
	
	@Override
	public FormQueryResult<NodeResult> list(NodeFormQuery formQuery, List<NodeResult> list) {
		NodeProjectionBuilder projectionBuilder = createProjectionBuilder(formQuery);
		FormQueryResult<NodeResult> result = createQueryResult(formQuery, projectionBuilder);
		return initResult(formQuery, result, list);
	}
	
	@Override
	public FormQueryResult<NodeResult> search(SearchFormQuery formQuery) {		
		RestrictionBuilder restrictionBuilder = new RestrictionBuilder(null, LogicalOperator.AND);
		formQuery.initRestrictions(restrictionBuilder);
		String luceneQuery = restrictionBuilder.toLuceneQuery();		

		NodeProjectionBuilder projectionBuilder = createProjectionBuilder(formQuery);
		NodeFetchDetails nodeFetchDetails = createNodeFetchDetails(projectionBuilder);

		List<NodeResult> list = nodeSearchService.search(luceneQuery, nodeFetchDetails);
		
		FormQueryResult<NodeResult> result = createQueryResult(formQuery, projectionBuilder);
		return initResult(formQuery, result, list);
	}

	@Override
	public FormQueryResult<NodeResult> children(NodeFormQuery formQuery, NodeReference parent, NameReference nameReference) {
		NodeProjectionBuilder projectionBuilder = createProjectionBuilder(formQuery);
		NodeFetchDetails nodeFetchDetails = createNodeFetchDetails(projectionBuilder);

		List<NodeResult> list = nodeSearchService.getChildren(parent, nameReference, nodeFetchDetails);
		FormQueryResult<NodeResult> result = createQueryResult(formQuery, projectionBuilder);
		return initResult(formQuery, result, list);
	}
	
	private NodeProjectionBuilder createProjectionBuilder(NodeFormQuery formQuery) {
		NodeProjectionBuilder projectionBuilder = new NodeProjectionBuilder();
		formQuery.initNodeProjections(projectionBuilder);
		return projectionBuilder;
	}
	
	private NodeFetchDetails createNodeFetchDetails(NodeProjectionBuilder projectionBuilder) {
		NodeFetchDetails nodeFetchDetails = new NodeFetchDetails();
		for (Projection<NodeResult, ?, ?> projection : projectionBuilder.getProjections()) {
			if (projection instanceof NodeProjection) {
				((NodeProjection<?>) projection).initNodeFetchDetails(nodeFetchDetails);
			}
		}
		return nodeFetchDetails;
	}
}
