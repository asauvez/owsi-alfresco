package fr.openwide.alfresco.query.core.search.service;

import java.util.List;

import fr.openwide.alfresco.query.core.node.model.value.NodeReference;
import fr.openwide.alfresco.query.core.search.model.NodeResult;

public interface NodeSearchService {

	List<NodeResult> search(String luceneQuery);

	List<NodeResult> getChildren(NodeReference nodeReference);

}
