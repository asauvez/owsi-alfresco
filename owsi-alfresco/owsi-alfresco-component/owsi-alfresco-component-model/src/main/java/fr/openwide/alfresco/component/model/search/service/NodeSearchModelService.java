package fr.openwide.alfresco.component.model.search.service;

import java.util.List;

import fr.openwide.alfresco.api.core.node.exception.NoSuchNodeRemoteException;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.component.model.search.SearchBuilder;
import fr.openwide.alfresco.component.model.search.restriction.RestrictionBuilder;

public interface NodeSearchModelService {

	List<BusinessNode> search(RestrictionBuilder restrictionBuilder, NodeScopeBuilder nodeScopeBuilder);
	List<BusinessNode> search(SearchBuilder searchBuilder, NodeScopeBuilder nodeScopeBuilder);

	BusinessNode searchUnique(RestrictionBuilder restrictionBuilder, NodeScopeBuilder nodeScopeBuilder) throws NoSuchNodeRemoteException;
	NodeReference searchUniqueRef(RestrictionBuilder restrictionBuilder) throws NoSuchNodeRemoteException;

}
