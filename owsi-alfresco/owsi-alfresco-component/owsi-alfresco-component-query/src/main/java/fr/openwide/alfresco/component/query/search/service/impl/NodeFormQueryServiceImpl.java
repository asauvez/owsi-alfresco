package fr.openwide.alfresco.component.query.search.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.app.core.node.service.NodeService;
import fr.openwide.alfresco.app.core.search.service.NodeSearchService;
import fr.openwide.alfresco.component.model.node.model.AssociationModel;
import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.component.model.search.restriction.RestrictionBuilder;
import fr.openwide.alfresco.component.query.form.projection.ProjectionVisitor;
import fr.openwide.alfresco.component.query.form.projection.node.NodeProjectionBuilder;
import fr.openwide.alfresco.component.query.form.projection.node.NodeScopeInitializer;
import fr.openwide.alfresco.component.query.form.result.FormQueryResult;
import fr.openwide.alfresco.component.query.search.model.NodeFormQuery;
import fr.openwide.alfresco.component.query.search.model.SearchFormQuery;
import fr.openwide.alfresco.component.query.search.service.NodeFormQueryService;

public class NodeFormQueryServiceImpl extends AbstractFormQueryService implements NodeFormQueryService {

	@Autowired
	private NodeService nodeService;
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
		RestrictionBuilder restrictionBuilder = new RestrictionBuilder();
		formQuery.initRestrictions(restrictionBuilder);

		NodeProjectionBuilder projectionBuilder = createProjectionBuilder(formQuery);
		NodeScope nodeScope = createNodeScope(projectionBuilder);

		List<RepositoryNode> list = nodeSearchService.search(
				restrictionBuilder.toLuceneQuery(),
				formQuery.getStoreReference(),
				nodeScope);

		FormQueryResult<RepositoryNode> result = createQueryResult(formQuery, projectionBuilder);
		return initResult(formQuery, result, list);
	}

	@Override
	public FormQueryResult<RepositoryNode> getChildren(NodeFormQuery formQuery, NodeReference parent, ChildAssociationModel childAssoc) {
		NodeProjectionBuilder projectionBuilder = createProjectionBuilder(formQuery);
		NodeScope nodeScope = createNodeScope(projectionBuilder);

		List<RepositoryNode> list = nodeService.getChildren(parent, childAssoc.getNameReference(), nodeScope);
		FormQueryResult<RepositoryNode> result = createQueryResult(formQuery, projectionBuilder);
		return initResult(formQuery, result, list);
	}

	@Override
	public FormQueryResult<RepositoryNode> getChildren(NodeFormQuery formQuery, NodeReference parent) {
		return getChildren(formQuery, parent, CmModel.folder.contains);
	}
	
	@Override
	public FormQueryResult<RepositoryNode> getTargetAssocs(NodeFormQuery formQuery, NodeReference parent, AssociationModel assoc) {
		NodeProjectionBuilder projectionBuilder = createProjectionBuilder(formQuery);
		NodeScope nodeScope = createNodeScope(projectionBuilder);

		List<RepositoryNode> list = nodeService.getTargetAssocs(parent, assoc.getNameReference(), nodeScope);
		FormQueryResult<RepositoryNode> result = createQueryResult(formQuery, projectionBuilder);
		return initResult(formQuery, result, list);
	}

	@Override
	public FormQueryResult<RepositoryNode> getSourceAssocs(NodeFormQuery formQuery, NodeReference parent, AssociationModel assoc) {
		NodeProjectionBuilder projectionBuilder = createProjectionBuilder(formQuery);
		NodeScope nodeScope = createNodeScope(projectionBuilder);

		List<RepositoryNode> list = nodeService.getSourceAssocs(parent, assoc.getNameReference(), nodeScope);
		FormQueryResult<RepositoryNode> result = createQueryResult(formQuery, projectionBuilder);
		return initResult(formQuery, result, list);
	}

	private NodeProjectionBuilder createProjectionBuilder(NodeFormQuery formQuery) {
		NodeProjectionBuilder projectionBuilder = new NodeProjectionBuilder();
		formQuery.initNodeProjections(projectionBuilder);
		return projectionBuilder;
	}

	private NodeScope createNodeScope(NodeProjectionBuilder projectionBuilder) {
		final NodeScope nodeScope = new NodeScope();
		projectionBuilder.accept(new ProjectionVisitor() {
			@Override
			public void visitObject(Object o) {
				if (o instanceof NodeScopeInitializer) {
					((NodeScopeInitializer) o).initNodeScope(nodeScope);
				}
			}
		});
		return nodeScope;
	}
}
