package fr.openwide.alfresco.component.model.search.service;

import java.util.List;

import com.google.common.base.Optional;

import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.component.model.search.SearchBuilder;
import fr.openwide.alfresco.component.model.search.restriction.RestrictionBuilder;

public interface NodeSearchModelService {

	List<BusinessNode> search(RestrictionBuilder restrictionBuilder, NodeScopeBuilder nodeScopeBuilder);
	List<BusinessNode> search(SearchBuilder searchBuilder, NodeScopeBuilder nodeScopeBuilder);

	Optional<BusinessNode> searchUnique(RestrictionBuilder restrictionBuilder, NodeScopeBuilder nodeScopeBuilder);
	Optional<NodeReference> searchUniqueReference(RestrictionBuilder restrictionBuilder);

}
