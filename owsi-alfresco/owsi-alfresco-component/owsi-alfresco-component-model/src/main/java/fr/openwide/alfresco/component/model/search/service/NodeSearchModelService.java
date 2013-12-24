package fr.openwide.alfresco.component.model.search.service;

import java.util.List;

import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.NodeFetchDetailsBuilder;
import fr.openwide.alfresco.component.model.search.restriction.RestrictionBuilder;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

/**
 * Permet de faire des recherches de noeuds.
 * 
 * @author asauvez
 */
public interface NodeSearchModelService {

	List<BusinessNode> search(RestrictionBuilder builder, NodeFetchDetailsBuilder nodeFetchDetails);

	BusinessNode searchUnique(RestrictionBuilder builder, NodeFetchDetailsBuilder nodeFetchDetails);

	NodeReference searchUniqueRef(RestrictionBuilder builder);

}
