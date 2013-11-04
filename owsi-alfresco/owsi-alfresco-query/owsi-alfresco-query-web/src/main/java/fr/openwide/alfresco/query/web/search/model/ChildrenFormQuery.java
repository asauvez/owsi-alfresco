package fr.openwide.alfresco.query.web.search.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import fr.openwide.alfresco.query.core.node.model.value.NodeReference;
import fr.openwide.alfresco.query.core.search.model.NodeResult;
import fr.openwide.alfresco.query.core.search.service.NodeSearchService;

public abstract class ChildrenFormQuery extends NodeFormQuery {

	@Autowired
	private NodeSearchService searchService;

	public abstract NodeReference getParent();

	@Override
	protected List<NodeResult> retrieveResults() {
		return searchService.getChildren(getParent());
	}

}
