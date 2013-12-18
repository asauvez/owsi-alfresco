package fr.openwide.alfresco.query.core.search.service;

import java.util.List;

import fr.openwide.alfresco.query.core.node.model.AssociationModel;
import fr.openwide.alfresco.query.core.node.model.ChildAssociationModel;
import fr.openwide.alfresco.query.core.search.restriction.RestrictionBuilder;
import fr.openwide.alfresco.query.core.search.util.NodeFetchDetailsBuilder;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;
import fr.openwide.alfresco.repository.api.search.service.NodeSearchRemoteService;

public interface NodeSearchService extends NodeSearchRemoteService {

	List<RepositoryNode> search(RestrictionBuilder builder, NodeFetchDetailsBuilder nodeFetchDetails);
	
	RepositoryNode searchUnique(RestrictionBuilder builder, NodeFetchDetailsBuilder nodeFetchDetails);
	
	List<RepositoryNode> getChildren(NodeReference nodeReference, NodeFetchDetailsBuilder nodeFetchDetails);
	
	List<RepositoryNode> getChildren(NodeReference nodeReference, ChildAssociationModel childAssoc, NodeFetchDetailsBuilder nodeFetchDetails);
	
	List<RepositoryNode> getTargetAssocs(NodeReference nodeReference, AssociationModel assoc, NodeFetchDetailsBuilder nodeFetchDetails);
	
	List<RepositoryNode> getSourceAssocs(NodeReference nodeReference, AssociationModel assoc, NodeFetchDetailsBuilder nodeFetchDetails);
}
