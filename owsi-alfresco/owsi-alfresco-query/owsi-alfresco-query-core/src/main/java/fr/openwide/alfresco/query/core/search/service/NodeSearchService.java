package fr.openwide.alfresco.query.core.search.service;

import java.util.List;

import fr.openwide.alfresco.query.core.node.model.value.NameReference;
import fr.openwide.alfresco.query.core.node.model.value.NodeReference;
import fr.openwide.alfresco.query.core.search.model.NodeFetchDetails;
import fr.openwide.alfresco.query.core.search.model.NodeResult;

public interface NodeSearchService {

	List<NodeResult> search(String luceneQuery, NodeFetchDetails nodeFetchDetails);

	List<NodeResult> getChildren(NodeReference nodeReference, NameReference assocName, NodeFetchDetails nodeFetchDetails);

}
