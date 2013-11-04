package fr.openwide.alfresco.query.web.search.model;

import java.util.List;

import fr.openwide.alfresco.query.core.repository.model.CmModel;
import fr.openwide.alfresco.query.core.search.model.NodeResult;
import fr.openwide.alfresco.query.core.search.projection.Projection;
import fr.openwide.alfresco.query.core.search.projection.ProjectionBuilder;
import fr.openwide.alfresco.query.web.form.result.ColumnFormQueryResult;
import fr.openwide.alfresco.query.web.form.result.FormQueryResult;

public abstract class NodeFormQuery extends AbstractFormQuery<NodeResult> {

	private ProjectionBuilder projectionBuilder;

	public void initProjections(ProjectionBuilder builder) {
		builder
			.ref().of()
			.type().of()
			.prop(CmModel.object.name).of();
	}

	protected boolean filterResult(NodeResult node) {
		// to override
		return true;
	}

	protected abstract List<NodeResult> retrieveResults();

	@Override
	protected FormQueryResult<NodeResult> computeResults() {
		projectionBuilder = new ProjectionBuilder(getDefaultResultFormatter());
		initProjections(projectionBuilder);
		FormQueryResult<NodeResult> result = new FormQueryResult<NodeResult>();
		for (Projection<?> projection : projectionBuilder.getProjections()) {
			if (projection.isVisible()) {
				ColumnFormQueryResult<NodeResult> column = new ColumnFormQueryResult<NodeResult>(projection.getMessage(), projection.getMessageArgs())
					//.resultFormatter(projection)
					.align(projection.getAlign().name().toLowerCase())
					//.comparator(projection.getComparator());
					;
				result.getColumns().add(column);
			}
		}
		List<NodeResult> nodes = retrieveResults();
		for (NodeResult node : nodes) {
			if (filterResult(node)) {
				result.getRows().add(node);
			}
		}
		return result;
	}
}
