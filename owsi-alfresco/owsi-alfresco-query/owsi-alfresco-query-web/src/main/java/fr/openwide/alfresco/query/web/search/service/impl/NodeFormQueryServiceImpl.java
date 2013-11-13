package fr.openwide.alfresco.query.web.search.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Service;

import fr.openwide.alfresco.query.core.node.model.value.NameReference;
import fr.openwide.alfresco.query.core.node.model.value.NodeReference;
import fr.openwide.alfresco.query.core.search.model.NodeFetchDetails;
import fr.openwide.alfresco.query.core.search.model.NodeResult;
import fr.openwide.alfresco.query.core.search.restriction.RestrictionBuilder;
import fr.openwide.alfresco.query.core.search.restriction.RestrictionBuilder.LogicalOperator;
import fr.openwide.alfresco.query.core.search.service.NodeSearchService;
import fr.openwide.alfresco.query.web.form.projection.Projection;
import fr.openwide.alfresco.query.web.form.projection.ProjectionBuilder;
import fr.openwide.alfresco.query.web.form.result.ColumnFormQueryResult;
import fr.openwide.alfresco.query.web.form.result.FormQueryResult;
import fr.openwide.alfresco.query.web.form.util.MessageUtils;
import fr.openwide.alfresco.query.web.search.model.NodeFormQuery;
import fr.openwide.alfresco.query.web.search.model.SearchFormQuery;
import fr.openwide.alfresco.query.web.search.service.NodeFormQueryService;

@Service
public class NodeFormQueryServiceImpl extends AbstractFormQueryService implements NodeFormQueryService {
	
	@Autowired private NodeSearchService nodeSearchService;
	
	@Override
	public FormQueryResult<NodeResult> list(NodeFormQuery formQuery, List<NodeResult> list) {
		ProjectionBuilder projectionBuilder = createProjectionBuilder(formQuery);
		FormQueryResult<NodeResult> result = createQueryResult(formQuery, projectionBuilder);
		return initResult(formQuery, result, list);
	}
	
	@Override
	public FormQueryResult<NodeResult> search(SearchFormQuery formQuery) {		
		RestrictionBuilder restrictionBuilder = new RestrictionBuilder(null, LogicalOperator.AND);
		formQuery.initRestrictions(restrictionBuilder);
		String luceneQuery = restrictionBuilder.toLuceneQuery();		

		ProjectionBuilder projectionBuilder = createProjectionBuilder(formQuery);
		NodeFetchDetails nodeFetchDetails = createNodeFetchDetails(projectionBuilder);

		List<NodeResult> list = nodeSearchService.search(luceneQuery, nodeFetchDetails);
		
		FormQueryResult<NodeResult> result = createQueryResult(formQuery, projectionBuilder);
		return initResult(formQuery, result, list);
	}

	@Override
	public FormQueryResult<NodeResult> children(NodeFormQuery formQuery, NodeReference parent, NameReference nameReference) {
		ProjectionBuilder projectionBuilder = createProjectionBuilder(formQuery);
		NodeFetchDetails nodeFetchDetails = createNodeFetchDetails(projectionBuilder);

		List<NodeResult> list = nodeSearchService.getChildren(parent, nameReference, nodeFetchDetails);
		FormQueryResult<NodeResult> result = createQueryResult(formQuery, projectionBuilder);
		return initResult(formQuery, result, list);
	}
	
	private FormQueryResult<NodeResult> createQueryResult(NodeFormQuery formQuery, ProjectionBuilder projectionBuilder) {
		FormQueryResult<NodeResult> result = new FormQueryResult<NodeResult>();
		for (Projection<?> projection : projectionBuilder.getProjections()) {
			if (projection.isVisible()) {
				MessageSourceResolvable label = projection.getLabel();
				if (label == null) {
					label = MessageUtils.codes(
						formQuery.getClass().getName() + "." + projection.getDefaultLabelCode(),
						formQuery.getClass().getSimpleName() + "." + projection.getDefaultLabelCode(),
						projection.getDefaultLabelCode());
				}
				
				ColumnFormQueryResult<NodeResult> column = new ColumnFormQueryResult<NodeResult>(
						label, 
						projection.getOutputFieldView())
					.transformer(projection.getNodeTransformer())
					.sort(projection.getSortDirection(), projection.getSortPriority())
					.align("text-" + projection.getAlign().name().toLowerCase())
					.comparator(projection.getNodeComparator());
					;
				result.getColumns().add(column);
			}
		}
		return result;
	}

	private ProjectionBuilder createProjectionBuilder(NodeFormQuery formQuery) {
		ProjectionBuilder projectionBuilder = new ProjectionBuilder();
		formQuery.initProjections(projectionBuilder);
		return projectionBuilder;
	}
	
	private NodeFetchDetails createNodeFetchDetails(ProjectionBuilder projectionBuilder) {
		NodeFetchDetails nodeFetchDetails = new NodeFetchDetails();
		for (Projection<?> projection : projectionBuilder.getProjections()) {
			projection.initNodeFetchDetails(nodeFetchDetails);
		}
		return nodeFetchDetails;
	}
}
