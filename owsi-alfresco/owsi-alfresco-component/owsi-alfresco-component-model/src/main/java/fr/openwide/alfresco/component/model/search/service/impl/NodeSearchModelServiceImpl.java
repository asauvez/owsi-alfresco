package fr.openwide.alfresco.component.model.search.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import fr.openwide.alfresco.app.core.search.service.NodeSearchService;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.NodeFetchDetailsBuilder;
import fr.openwide.alfresco.component.model.search.restriction.RestrictionBuilder;
import fr.openwide.alfresco.component.model.search.service.NodeSearchModelService;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

public class NodeSearchModelServiceImpl implements NodeSearchModelService {

	@Autowired
	private NodeSearchService nodeSearchService;

	@Override
	public List<BusinessNode> search(RestrictionBuilder builder, NodeFetchDetailsBuilder nodeFetchDetails) {
		return BusinessNode.wrapList(nodeSearchService.search(builder.toLuceneQuery(), nodeFetchDetails.getDetails()));
	}

	@Override
	public BusinessNode searchUnique(RestrictionBuilder builder, NodeFetchDetailsBuilder nodeFetchDetails) {
		List<BusinessNode> list = search(builder, nodeFetchDetails);
		if (list.size() > 1) {
			throw new IllegalStateException("More than one result for " + builder.toLuceneQuery());
		}
		return (list.isEmpty()) ? null : list.get(0);
	}

	@Override
	public NodeReference searchUniqueRef(RestrictionBuilder builder) {
		BusinessNode node = searchUnique(builder, new NodeFetchDetailsBuilder()
				.nodeReference());
		return (node != null) ? node.getNodeReference() : null;
	}

}
