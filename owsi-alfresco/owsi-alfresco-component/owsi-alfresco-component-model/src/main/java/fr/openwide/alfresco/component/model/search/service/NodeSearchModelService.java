package fr.openwide.alfresco.component.model.search.service;

import java.util.List;

import fr.openwide.alfresco.component.model.node.model.AssociationModel;
import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.search.restriction.RestrictionBuilder;
import fr.openwide.alfresco.component.model.search.util.NodeFetchDetailsBuilder;
import fr.openwide.alfresco.component.model.search.util.BusinessNode;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

public interface NodeSearchModelService {

	BusinessNode get(NodeReference nodeReference, NodeFetchDetailsBuilder nodeFetchDetails);

	List<BusinessNode> search(RestrictionBuilder builder, NodeFetchDetailsBuilder nodeFetchDetails);
	
	BusinessNode searchUnique(RestrictionBuilder builder, NodeFetchDetailsBuilder nodeFetchDetails);
	
	NodeReference searchUniqueRef(RestrictionBuilder builder);
	
	List<BusinessNode> getChildren(NodeReference nodeReference, NodeFetchDetailsBuilder nodeFetchDetails);
	
	List<BusinessNode> getChildren(NodeReference nodeReference, ChildAssociationModel childAssoc, NodeFetchDetailsBuilder nodeFetchDetails);
	
	List<BusinessNode> getTargetAssocs(NodeReference nodeReference, AssociationModel assoc, NodeFetchDetailsBuilder nodeFetchDetails);
	
	List<BusinessNode> getSourceAssocs(NodeReference nodeReference, AssociationModel assoc, NodeFetchDetailsBuilder nodeFetchDetails);
}
