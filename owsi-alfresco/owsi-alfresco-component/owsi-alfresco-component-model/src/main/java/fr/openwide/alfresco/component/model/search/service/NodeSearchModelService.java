package fr.openwide.alfresco.component.model.search.service;

import java.util.List;

import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.component.model.search.restriction.RestrictionBuilder;
import fr.openwide.alfresco.api.core.node.exception.NoSuchNodeRemoteException;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.api.core.remote.model.StoreReference;

/**
 * Permet de faire des recherches de noeuds.
 * 
 * @author asauvez
 */
public interface NodeSearchModelService {

	List<BusinessNode> search(RestrictionBuilder builder, NodeScopeBuilder nodeScopeBuilder);
	
	List<BusinessNode> search(RestrictionBuilder builder, StoreReference storeReference, NodeScopeBuilder nodeScopeBuilder);

	BusinessNode searchUnique(RestrictionBuilder builder, NodeScopeBuilder nodeScopeBuilder) throws NoSuchNodeRemoteException;

	NodeReference searchUniqueRef(RestrictionBuilder builder) throws NoSuchNodeRemoteException;

}
