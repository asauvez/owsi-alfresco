package fr.openwide.alfresco.query.web.search.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
import fr.openwide.alfresco.repository.api.node.model.NodeFetchDetails;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

@Service
public class NodeFormQueryServiceImpl extends AbstractFormQueryService implements NodeFormQueryService {

	@Autowired
	private NodeSearchService nodeSearchService;

	@Override
	public FormQueryResult<RepositoryNode> list(NodeFormQuery formQuery, List<RepositoryNode> list) {
		NodeProjectionBuilder projectionBuilder = createProjectionBuilder(formQuery);
		FormQueryResult<RepositoryNode> result = createQueryResult(formQuery, projectionBuilder);
		return initResult(formQuery, result, list);
	}

	@Override
	public FormQueryResult<RepositoryNode> search(SearchFormQuery formQuery) {
		RestrictionBuilder restrictionBuilder = new RestrictionBuilder(null, LogicalOperator.AND);
		formQuery.initRestrictions(restrictionBuilder);
		String luceneQuery = restrictionBuilder.toLuceneQuery();

		NodeProjectionBuilder projectionBuilder = createProjectionBuilder(formQuery);
		NodeFetchDetails nodeFetchDetails = createNodeFetchDetails(projectionBuilder);

		List<RepositoryNode> list = nodeSearchService.search(luceneQuery, nodeFetchDetails);

		FormQueryResult<RepositoryNode> result = createQueryResult(formQuery, projectionBuilder);
		return initResult(formQuery, result, list);
	}

	@Override
	public FormQueryResult<RepositoryNode> getChildren(NodeFormQuery formQuery, NodeReference parent, NameReference childAssocName) {
		NodeProjectionBuilder projectionBuilder = createProjectionBuilder(formQuery);
		NodeFetchDetails nodeFetchDetails = createNodeFetchDetails(projectionBuilder);

		List<RepositoryNode> list = nodeSearchService.getChildren(parent, childAssocName, nodeFetchDetails);
		FormQueryResult<RepositoryNode> result = createQueryResult(formQuery, projectionBuilder);
		return initResult(formQuery, result, list);
	}

	@Override
	public FormQueryResult<RepositoryNode> getTargetAssocs(NodeFormQuery formQuery, NodeReference parent, NameReference assocName) {
		NodeProjectionBuilder projectionBuilder = createProjectionBuilder(formQuery);
		NodeFetchDetails nodeFetchDetails = createNodeFetchDetails(projectionBuilder);

		List<RepositoryNode> list = nodeSearchService.getTargetAssocs(parent, assocName, nodeFetchDetails);
		FormQueryResult<RepositoryNode> result = createQueryResult(formQuery, projectionBuilder);
		return initResult(formQuery, result, list);
	}

	@Override
	public FormQueryResult<RepositoryNode> getSourceAssocs(NodeFormQuery formQuery, NodeReference parent, NameReference assocName) {
		NodeProjectionBuilder projectionBuilder = createProjectionBuilder(formQuery);
		NodeFetchDetails nodeFetchDetails = createNodeFetchDetails(projectionBuilder);

		List<RepositoryNode> list = nodeSearchService.getSourceAssocs(parent, assocName, nodeFetchDetails);
		FormQueryResult<RepositoryNode> result = createQueryResult(formQuery, projectionBuilder);
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
