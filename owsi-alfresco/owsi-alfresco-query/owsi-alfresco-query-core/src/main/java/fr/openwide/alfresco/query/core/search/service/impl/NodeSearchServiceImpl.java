package fr.openwide.alfresco.query.core.search.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;

import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryRemoteBinding;
import fr.openwide.alfresco.query.core.node.model.AssociationModel;
import fr.openwide.alfresco.query.core.node.model.ChildAssociationModel;
import fr.openwide.alfresco.query.core.repository.model.CmModel;
import fr.openwide.alfresco.query.core.search.restriction.RestrictionBuilder;
import fr.openwide.alfresco.query.core.search.service.NodeSearchService;
import fr.openwide.alfresco.query.core.search.util.NodeFetchDetailsBuilder;
import fr.openwide.alfresco.repository.api.node.model.NodeFetchDetails;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.exception.RepositoryRemoteException;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

public class NodeSearchServiceImpl implements NodeSearchService {

	@Autowired
	private RepositoryRemoteBinding repositoryRemoteBinding;

	@Override
	public RepositoryNode get(NodeReference nodeReference, NodeFetchDetails nodeFetchDetails) {
		try {
			return repositoryRemoteBinding.exchange(GET_NODE_SERVICE.URL, 
					GET_NODE_SERVICE.METHOD, RepositoryNode.class);
		} catch (RepositoryRemoteException e) {
			// do not deal with other types of remote exception
			throw new IllegalStateException(e);
		}
	}

	@Override
	public List<RepositoryNode> search(RestrictionBuilder builder, NodeFetchDetailsBuilder nodeFetchDetails) {
		return search(builder.toLuceneQuery(), nodeFetchDetails.getDetails());
	}

	@Override
	public RepositoryNode searchUnique(RestrictionBuilder builder, NodeFetchDetailsBuilder nodeFetchDetails) {
		List<RepositoryNode> list = search(builder, nodeFetchDetails);
		if (list.size() > 1) {
			throw new IllegalStateException("More than one result for " + builder.toLuceneQuery());
		}
		return (list.isEmpty()) ? null : list.get(0);
	}


	@Override
	public List<RepositoryNode> getChildren(NodeReference nodeReference, NodeFetchDetailsBuilder nodeFetchDetails) {
		return getChildren(nodeReference, CmModel.folder.contains, nodeFetchDetails);
	}
	
	@Override
	public List<RepositoryNode> getChildren(NodeReference nodeReference, ChildAssociationModel childAssoc, NodeFetchDetailsBuilder nodeFetchDetails) {
		return getChildren(nodeReference, childAssoc.getNameReference(), nodeFetchDetails.getDetails());
	}

	@Override
	public List<RepositoryNode> getTargetAssocs(NodeReference nodeReference, AssociationModel assoc, NodeFetchDetailsBuilder nodeFetchDetails) {
		return getTargetAssocs(nodeReference, assoc.getNameReference(), nodeFetchDetails.getDetails());
	}

	@Override
	public List<RepositoryNode> getSourceAssocs(NodeReference nodeReference, AssociationModel assoc, NodeFetchDetailsBuilder nodeFetchDetails) {
		return getSourceAssocs(nodeReference, assoc.getNameReference(), nodeFetchDetails.getDetails());
	}

	@Override
	public List<RepositoryNode> search(String query, NodeFetchDetails nodeFetchDetails) {
		try {
			SEARCH_NODE_SERVICE request = new SEARCH_NODE_SERVICE();
			request.query = query;
			request.nodeFetchDetails = nodeFetchDetails;
			return repositoryRemoteBinding.exchangeCollection(SEARCH_NODE_SERVICE.URL, 
					SEARCH_NODE_SERVICE.METHOD, request, new ParameterizedTypeReference<List<RepositoryNode>>() {});
		} catch (RepositoryRemoteException e) {
			// do not deal with other types of remote exception
			throw new IllegalStateException(e);
		}
	}

	@Override
	public List<RepositoryNode> getChildren(NodeReference nodeReference, NameReference childAssocName, NodeFetchDetails nodeFetchDetails) {
		try {
			CHILDREN_NODE_SERVICE request = new CHILDREN_NODE_SERVICE();
			request.nodeReference = nodeReference;
			request.childAssocName = childAssocName;
			request.nodeFetchDetails = nodeFetchDetails;
			return repositoryRemoteBinding.exchangeCollection(CHILDREN_NODE_SERVICE.URL, 
					CHILDREN_NODE_SERVICE.METHOD, request, new ParameterizedTypeReference<List<RepositoryNode>>() {});
		} catch (RepositoryRemoteException e) {
			// do not deal with other types of remote exception
			throw new IllegalStateException(e);
		}
	}
	
	@Override
	public List<RepositoryNode> getTargetAssocs(NodeReference nodeReference, NameReference assocName, NodeFetchDetails nodeFetchDetails) {
		try {
			TARGET_ASSOC_NODE_SERVICE request = new TARGET_ASSOC_NODE_SERVICE();
			request.nodeReference = nodeReference;
			request.assocName = assocName;
			request.nodeFetchDetails = nodeFetchDetails;
			return repositoryRemoteBinding.exchangeCollection(TARGET_ASSOC_NODE_SERVICE.URL, 
					TARGET_ASSOC_NODE_SERVICE.METHOD, request, new ParameterizedTypeReference<List<RepositoryNode>>() {});
		} catch (RepositoryRemoteException e) {
			// do not deal with other types of remote exception
			throw new IllegalStateException(e);
		}
	}

	@Override
	public List<RepositoryNode> getSourceAssocs(NodeReference nodeReference, NameReference assocName, NodeFetchDetails nodeFetchDetails) {
		try {
			SOURCE_ASSOC_NODE_SERVICE request = new SOURCE_ASSOC_NODE_SERVICE();
			request.nodeReference = nodeReference;
			request.assocName = assocName;
			request.nodeFetchDetails = nodeFetchDetails;
			return repositoryRemoteBinding.exchangeCollection(SOURCE_ASSOC_NODE_SERVICE.URL, 
					SOURCE_ASSOC_NODE_SERVICE.METHOD, request, new ParameterizedTypeReference<List<RepositoryNode>>() {});
		} catch (RepositoryRemoteException e) {
			// do not deal with other types of remote exception
			throw new IllegalStateException(e);
		}
	}

}
