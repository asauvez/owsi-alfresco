package fr.openwide.alfresco.component.model.search.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import fr.openwide.alfresco.app.core.search.service.NodeSearchService;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.component.model.search.restriction.RestrictionBuilder;
import fr.openwide.alfresco.component.model.search.service.NodeSearchModelService;
import fr.openwide.alfresco.repository.api.node.exception.NoSuchNodeException;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;
import fr.openwide.alfresco.repository.api.remote.model.StoreReference;

public class NodeSearchModelServiceImpl implements NodeSearchModelService {

	@Autowired
	private NodeSearchService nodeSearchService;

	@Override
	public List<BusinessNode> search(RestrictionBuilder builder, NodeScopeBuilder nodeScopeBuilder) {
		return search(builder, StoreReference.STORE_REF_WORKSPACE_SPACESSTORE, nodeScopeBuilder);
	}

	@Override
	public List<BusinessNode> search(RestrictionBuilder builder, StoreReference storeReference,
			NodeScopeBuilder nodeScopeBuilder) {
		return BusinessNode.wrapList(nodeSearchService.search(
				builder.toLuceneQuery(),
				storeReference,
				nodeScopeBuilder.getScope()));
	}

	@Override
	public BusinessNode searchUnique(RestrictionBuilder builder, NodeScopeBuilder nodeScopeBuilder) throws NoSuchNodeException {
		List<BusinessNode> list = search(builder, nodeScopeBuilder);
		if (list.size() > 1) {
			throw new IllegalStateException("More than one result for " + builder.toLuceneQuery());
		} else if (list.isEmpty()) {
			throw new NoSuchNodeException(builder.toLuceneQuery());
		}
		return list.get(0);
	}

	@Override
	public NodeReference searchUniqueRef(RestrictionBuilder builder) throws NoSuchNodeException {
		BusinessNode node = searchUnique(builder, new NodeScopeBuilder()
				.nodeReference());
		return (node != null) ? node.getNodeReference() : null;
	}

}
