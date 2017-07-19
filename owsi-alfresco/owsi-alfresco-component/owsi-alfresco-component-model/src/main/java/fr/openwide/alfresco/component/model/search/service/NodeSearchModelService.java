package fr.openwide.alfresco.component.model.search.service;

import java.util.List;

import java.util.Optional;

import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.component.model.search.model.SearchQueryBuilder;
import fr.openwide.alfresco.component.model.search.model.restriction.RestrictionBuilder;

public interface NodeSearchModelService {

	List<BusinessNode> search(RestrictionBuilder restrictionBuilder, NodeScopeBuilder nodeScopeBuilder);
	List<BusinessNode> search(SearchQueryBuilder searchBuilder);

	Optional<BusinessNode> searchUnique(RestrictionBuilder restrictionBuilder, NodeScopeBuilder nodeScopeBuilder);
	Optional<NodeReference> searchUniqueReference(RestrictionBuilder restrictionBuilder);

}
