package fr.openwide.alfresco.query.web.search.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.openwide.alfresco.query.api.node.model.NameReference;
import fr.openwide.alfresco.query.api.node.model.NodeReference;
import fr.openwide.alfresco.query.api.search.model.NodeFetchDetails;
import fr.openwide.alfresco.query.api.search.model.NodeResult;
import fr.openwide.alfresco.query.core.search.restriction.RestrictionBuilder;
import fr.openwide.alfresco.query.core.search.restriction.RestrictionBuilder.LogicalOperator;
import fr.openwide.alfresco.query.core.search.service.NodeSearchService;
import fr.openwide.alfresco.query.web.form.projection.ProjectionVisitor;
import fr.openwide.alfresco.query.web.form.projection.node.NodeFetchDetailsInitializer;
import fr.openwide.alfresco.query.web.form.projection.node.NodeProjectionBuilder;
import fr.openwide.alfresco.query.web.form.result.FormQueryResult;
import fr.openwide.alfresco.query.web.search.model.NodeFormQuery;
import fr.openwide.alfresco.query.web.search.model.SearchFormQuery;
import fr.openwide.alfresco.query.web.search.service.NodeFormQueryService;

@Service
public class NodeFormQueryServiceImpl extends AbstractFormQueryService implements NodeFormQueryService {

	@Autowired
	private NodeSearchService nodeSearchService;

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
	public FormQueryResult<NodeResult> getChildren(NodeFormQuery formQuery, NodeReference parent, NameReference childAssocName) {
		NodeProjectionBuilder projectionBuilder = createProjectionBuilder(formQuery);
		NodeFetchDetails nodeFetchDetails = createNodeFetchDetails(projectionBuilder);

		List<NodeResult> list = nodeSearchService.getChildren(parent, childAssocName, nodeFetchDetails);
		FormQueryResult<NodeResult> result = createQueryResult(formQuery, projectionBuilder);
		return initResult(formQuery, result, list);
	}

	@Override
	public FormQueryResult<NodeResult> getTargetAssocs(NodeFormQuery formQuery, NodeReference parent, NameReference assocName) {
		NodeProjectionBuilder projectionBuilder = createProjectionBuilder(formQuery);
		NodeFetchDetails nodeFetchDetails = createNodeFetchDetails(projectionBuilder);

		List<NodeResult> list = nodeSearchService.getTargetAssocs(parent, assocName, nodeFetchDetails);
		FormQueryResult<NodeResult> result = createQueryResult(formQuery, projectionBuilder);
		return initResult(formQuery, result, list);
	}

	@Override
	public FormQueryResult<NodeResult> getSourceAssocs(NodeFormQuery formQuery, NodeReference parent, NameReference assocName) {
		NodeProjectionBuilder projectionBuilder = createProjectionBuilder(formQuery);
		NodeFetchDetails nodeFetchDetails = createNodeFetchDetails(projectionBuilder);

		List<NodeResult> list = nodeSearchService.getSourceAssocs(parent, assocName, nodeFetchDetails);
		FormQueryResult<NodeResult> result = createQueryResult(formQuery, projectionBuilder);
		return initResult(formQuery, result, list);
	}

	private NodeProjectionBuilder createProjectionBuilder(NodeFormQuery formQuery) {
		NodeProjectionBuilder projectionBuilder = new NodeProjectionBuilder();
		formQuery.initNodeProjections(projectionBuilder);
		return projectionBuilder;
	}

	private NodeFetchDetails createNodeFetchDetails(NodeProjectionBuilder projectionBuilder) {
		final NodeFetchDetails nodeFetchDetails = new NodeFetchDetails();
		projectionBuilder.accept(new ProjectionVisitor() {
			@Override
			public void visitObject(Object o) {
				if (o instanceof NodeFetchDetailsInitializer) {
					((NodeFetchDetailsInitializer) o).initNodeFetchDetails(nodeFetchDetails);
				}
			}
		});
		return nodeFetchDetails;
	}
}
