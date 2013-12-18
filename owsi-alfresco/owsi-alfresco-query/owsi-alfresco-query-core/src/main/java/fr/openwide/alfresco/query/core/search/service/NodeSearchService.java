package fr.openwide.alfresco.query.core.search.service;

import java.util.List;

import fr.openwide.alfresco.query.core.node.model.AssociationModel;
import fr.openwide.alfresco.query.core.node.model.ChildAssociationModel;
import fr.openwide.alfresco.query.core.search.restriction.RestrictionBuilder;
import fr.openwide.alfresco.query.core.search.util.NodeFetchDetailsBuilder;
import fr.openwide.alfresco.query.core.search.util.RepositoryNodeWrapper;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;
import fr.openwide.alfresco.repository.api.search.service.NodeSearchRemoteService;

public interface NodeSearchService extends NodeSearchRemoteService {

	RepositoryNodeWrapper get(NodeReference nodeReference, NodeFetchDetailsBuilder nodeFetchDetails);

	List<RepositoryNodeWrapper> search(RestrictionBuilder builder, NodeFetchDetailsBuilder nodeFetchDetails);
	
	RepositoryNodeWrapper searchUnique(RestrictionBuilder builder, NodeFetchDetailsBuilder nodeFetchDetails);
	
	List<RepositoryNodeWrapper> getChildren(NodeReference nodeReference, NodeFetchDetailsBuilder nodeFetchDetails);
	
	List<RepositoryNodeWrapper> getChildren(NodeReference nodeReference, ChildAssociationModel childAssoc, NodeFetchDetailsBuilder nodeFetchDetails);
	
	List<RepositoryNodeWrapper> getTargetAssocs(NodeReference nodeReference, AssociationModel assoc, NodeFetchDetailsBuilder nodeFetchDetails);
	
	List<RepositoryNodeWrapper> getSourceAssocs(NodeReference nodeReference, AssociationModel assoc, NodeFetchDetailsBuilder nodeFetchDetails);
}
