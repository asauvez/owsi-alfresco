package fr.openwide.alfresco.query.core.search.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryRemoteBinding;
import fr.openwide.alfresco.query.api.node.model.NameReference;
import fr.openwide.alfresco.query.api.node.model.NodeReference;
import fr.openwide.alfresco.query.api.search.model.NodeFetchDetails;
import fr.openwide.alfresco.query.api.search.model.NodeResult;
import fr.openwide.alfresco.query.core.search.service.NodeSearchService;
import fr.openwide.alfresco.repository.api.remote.model.RepositoryRemoteException;

@Service
public class NodeSearchServiceImpl implements NodeSearchService {

	@Autowired
	private RepositoryRemoteBinding repositoryRemoteBinding;

	@Override
	public NodeResult get(NodeReference nodeReference, NodeFetchDetails nodeFetchDetails) {
		try {
			return repositoryRemoteBinding.exchange(GET_NODE_SERVICE.URL, 
					GET_NODE_SERVICE.METHOD, NodeResult.class);
		} catch (RepositoryRemoteException e) {
			// do not deal with other types of remote exception
			throw new IllegalStateException(e);
		}
	}
	
	@Override
	public List<NodeResult> search(String query, NodeFetchDetails nodeFetchDetails) {
		try {
			SEARCH_NODE_SERVICE request = new SEARCH_NODE_SERVICE();
			request.query = query;
			request.nodeFetchDetails = nodeFetchDetails;
			return repositoryRemoteBinding.exchangeCollection(SEARCH_NODE_SERVICE.URL, 
					SEARCH_NODE_SERVICE.METHOD, request, new ParameterizedTypeReference<List<NodeResult>>() {});
		} catch (RepositoryRemoteException e) {
			// do not deal with other types of remote exception
			throw new IllegalStateException(e);
		}
	}

	@Override
	public List<NodeResult> getChildren(NodeReference nodeReference, NameReference childAssocName, NodeFetchDetails nodeFetchDetails) {
		try {
			CHILDREN_NODE_SERVICE request = new CHILDREN_NODE_SERVICE();
			request.nodeReference = nodeReference;
			request.childAssocName = childAssocName;
			request.nodeFetchDetails = nodeFetchDetails;
			return repositoryRemoteBinding.exchangeCollection(CHILDREN_NODE_SERVICE.URL, 
					CHILDREN_NODE_SERVICE.METHOD, request, new ParameterizedTypeReference<List<NodeResult>>() {});
		} catch (RepositoryRemoteException e) {
			// do not deal with other types of remote exception
			throw new IllegalStateException(e);
		}
	}

	@Override
	public List<NodeResult> getTargetAssocs(NodeReference nodeReference, NameReference assocName, NodeFetchDetails nodeFetchDetails) {
		try {
			TARGET_ASSOC_NODE_SERVICE request = new TARGET_ASSOC_NODE_SERVICE();
			request.nodeReference = nodeReference;
			request.assocName = assocName;
			request.nodeFetchDetails = nodeFetchDetails;
			return repositoryRemoteBinding.exchangeCollection(TARGET_ASSOC_NODE_SERVICE.URL, 
					TARGET_ASSOC_NODE_SERVICE.METHOD, request, new ParameterizedTypeReference<List<NodeResult>>() {});
		} catch (RepositoryRemoteException e) {
			// do not deal with other types of remote exception
			throw new IllegalStateException(e);
		}
	}

	@Override
	public List<NodeResult> getSourceAssocs(NodeReference nodeReference, NameReference assocName, NodeFetchDetails nodeFetchDetails) {
		try {
			SOURCE_ASSOC_NODE_SERVICE request = new SOURCE_ASSOC_NODE_SERVICE();
			request.nodeReference = nodeReference;
			request.assocName = assocName;
			request.nodeFetchDetails = nodeFetchDetails;
			return repositoryRemoteBinding.exchangeCollection(SOURCE_ASSOC_NODE_SERVICE.URL, 
					SOURCE_ASSOC_NODE_SERVICE.METHOD, request, new ParameterizedTypeReference<List<NodeResult>>() {});
		} catch (RepositoryRemoteException e) {
			// do not deal with other types of remote exception
			throw new IllegalStateException(e);
		}
	}

}
