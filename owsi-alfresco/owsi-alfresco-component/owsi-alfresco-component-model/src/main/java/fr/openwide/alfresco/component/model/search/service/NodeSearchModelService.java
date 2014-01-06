package fr.openwide.alfresco.component.model.search.service;

import java.util.List;

import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.NodeFetchDetailsBuilder;
import fr.openwide.alfresco.component.model.search.restriction.RestrictionBuilder;
import fr.openwide.alfresco.repository.api.node.exception.NoSuchNodeException;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;
import fr.openwide.alfresco.repository.api.remote.model.StoreReference;

/**
 * Permet de faire des recherches de noeuds.
 * 
 * @author asauvez
 */
public interface NodeSearchModelService {

	List<BusinessNode> search(RestrictionBuilder builder, NodeFetchDetailsBuilder nodeFetchDetails);
	
	List<BusinessNode> search(RestrictionBuilder builder, StoreReference storeReference, NodeFetchDetailsBuilder nodeFetchDetails);

	BusinessNode searchUnique(RestrictionBuilder builder, NodeFetchDetailsBuilder nodeFetchDetails) throws NoSuchNodeException;

	NodeReference searchUniqueRef(RestrictionBuilder builder) throws NoSuchNodeException;

}
