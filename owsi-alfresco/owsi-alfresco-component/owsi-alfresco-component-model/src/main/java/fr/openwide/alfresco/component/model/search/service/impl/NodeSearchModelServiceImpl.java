package fr.openwide.alfresco.component.model.search.service.impl;

import java.util.List;

import fr.openwide.alfresco.api.core.node.exception.NoSuchNodeRemoteException;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.app.core.search.service.NodeSearchService;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.BusinessNodeList;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.component.model.search.SearchBuilder;
import fr.openwide.alfresco.component.model.search.restriction.RestrictionBuilder;
import fr.openwide.alfresco.component.model.search.service.NodeSearchModelService;

public class NodeSearchModelServiceImpl implements NodeSearchModelService {

	private final NodeSearchService nodeSearchService;

	public NodeSearchModelServiceImpl(NodeSearchService nodeSearchService) {
		this.nodeSearchService = nodeSearchService;
	}

	@Override
	public List<BusinessNode> search(RestrictionBuilder restrictionBuilder, NodeScopeBuilder nodeScopeBuilder) {
		return search(new SearchBuilder()
				.restriction(restrictionBuilder),
			nodeScopeBuilder);
	}

	@Override
	public List<BusinessNode> search(SearchBuilder searchBuilder, NodeScopeBuilder nodeScopeBuilder) {
		return new BusinessNodeList(nodeSearchService.search(
				searchBuilder.getSearchParameters(),
				nodeScopeBuilder.getScope()));
	}
	@Override
	public BusinessNode searchUnique(RestrictionBuilder builder, NodeScopeBuilder nodeScopeBuilder) throws NoSuchNodeRemoteException {
		List<BusinessNode> list = search(builder, nodeScopeBuilder);
		if (list.size() > 1) {
			throw new IllegalStateException("More than one result for " + builder.toQuery());
		} else if (list.isEmpty()) {
			throw new NoSuchNodeRemoteException(builder.toQuery());
		}
		return list.get(0);
	}

	@Override
	public NodeReference searchUniqueRef(RestrictionBuilder builder) throws NoSuchNodeRemoteException {
		BusinessNode node = searchUnique(builder, new NodeScopeBuilder()
				.nodeReference());
		return (node != null) ? node.getNodeReference() : null;
	}

}
