package fr.openwide.alfresco.component.model.search.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import fr.openwide.alfresco.app.core.search.service.NodeSearchService;
import fr.openwide.alfresco.component.model.node.model.AssociationModel;
import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.component.model.search.restriction.RestrictionBuilder;
import fr.openwide.alfresco.component.model.search.service.NodeSearchModelService;
import fr.openwide.alfresco.component.model.search.util.BusinessNode;
import fr.openwide.alfresco.component.model.search.util.NodeFetchDetailsBuilder;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

public class NodeSearchModelServiceImpl implements NodeSearchModelService {

	@Autowired
	private NodeSearchService nodeSearchService;

	@Override
	public BusinessNode get(NodeReference nodeReference, NodeFetchDetailsBuilder nodeFetchDetails) {
		RepositoryNode node = nodeSearchService.get(nodeReference, nodeFetchDetails.getDetails());
		return (node != null) ? new BusinessNode(node) : null;
	}
	
	@Override
	public List<BusinessNode> search(RestrictionBuilder builder, NodeFetchDetailsBuilder nodeFetchDetails) {
		return wrapList(nodeSearchService.search(builder.toLuceneQuery(), nodeFetchDetails.getDetails()));
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

	@Override
	public List<BusinessNode> getChildren(NodeReference nodeReference, NodeFetchDetailsBuilder nodeFetchDetails) {
		return getChildren(nodeReference, CmModel.folder.contains, nodeFetchDetails);
	}
	
	@Override
	public List<BusinessNode> getChildren(NodeReference nodeReference, ChildAssociationModel childAssoc, NodeFetchDetailsBuilder nodeFetchDetails) {
		return wrapList(nodeSearchService.getChildren(nodeReference, childAssoc.getNameReference(), nodeFetchDetails.getDetails()));
	}

	@Override
	public List<BusinessNode> getTargetAssocs(NodeReference nodeReference, AssociationModel assoc, NodeFetchDetailsBuilder nodeFetchDetails) {
		return wrapList(nodeSearchService.getTargetAssocs(nodeReference, assoc.getNameReference(), nodeFetchDetails.getDetails()));
	}

	@Override
	public List<BusinessNode> getSourceAssocs(NodeReference nodeReference, AssociationModel assoc, NodeFetchDetailsBuilder nodeFetchDetails) {
		return wrapList(nodeSearchService.getSourceAssocs(nodeReference, assoc.getNameReference(), nodeFetchDetails.getDetails()));
	}

	private List<BusinessNode> wrapList(List<RepositoryNode> nodes) {
		ArrayList<BusinessNode> wrappers = new ArrayList<>();
		for (RepositoryNode node : nodes) {
			wrappers.add(new BusinessNode(node));
		}
		return wrappers;
	}

}
