package fr.openwide.alfresco.component.model.search.service.impl;

import java.util.List;

import com.google.common.base.Optional;

import fr.openwide.alfresco.api.core.node.exception.NoSuchNodeRemoteException;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.api.core.search.service.NodeSearchRemoteService;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.BusinessNodeList;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.component.model.search.model.SearchQueryBuilder;
import fr.openwide.alfresco.component.model.search.model.restriction.RestrictionBuilder;
import fr.openwide.alfresco.component.model.search.service.NodeSearchModelService;

public class NodeSearchModelServiceImpl implements NodeSearchModelService {

	private final NodeSearchRemoteService nodeSearchService;

	public NodeSearchModelServiceImpl(NodeSearchRemoteService nodeSearchService) {
		this.nodeSearchService = nodeSearchService;
	}

	@Override
	public List<BusinessNode> search(RestrictionBuilder restrictionBuilder, NodeScopeBuilder nodeScopeBuilder) {
		return search(new SearchQueryBuilder()
				.restriction(restrictionBuilder)
				.nodeScopeBuilder(nodeScopeBuilder));
	}

	@Override
	public List<BusinessNode> search(SearchQueryBuilder searchBuilder) {
		return new BusinessNodeList(nodeSearchService.search(
				searchBuilder.getParameters()));
	}
	
	@Override
	public Optional<BusinessNode> searchUnique(RestrictionBuilder restrictionBuilder, NodeScopeBuilder nodeScopeBuilder) throws NoSuchNodeRemoteException {
		List<BusinessNode> list = search(restrictionBuilder, nodeScopeBuilder);
		if (list.size() > 1) {
			throw new IllegalStateException("More than one result for " + restrictionBuilder.toFtsQuery());
		} else if (list.isEmpty()) {
			return Optional.absent();
		}
		return Optional.of(list.get(0));
	}

	@Override
	public Optional<NodeReference> searchUniqueReference(RestrictionBuilder restrictionBuilder) {
		Optional<BusinessNode> node = searchUnique(restrictionBuilder, new NodeScopeBuilder()
				.nodeReference());
		return node.isPresent() ? Optional.of(node.get().getNodeReference()) : Optional.fromNullable((NodeReference) null);
	}

}
