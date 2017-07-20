package fr.openwide.alfresco.repo.core.search.web.script;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.search.service.NodeSearchRemoteService;
import fr.openwide.alfresco.api.core.search.service.NodeSearchRemoteService.SEARCH_NODE_SERVICE;
import fr.openwide.alfresco.repo.core.node.web.script.AbstractNodeListWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptTransactionAllow;

@GenerateWebScript(
		paramClass=SEARCH_NODE_SERVICE.class,
		shortName="search",
		description="Search nodes with a Lucene query",
		formatDefault="json",
		transactionAllow=GenerateWebScriptTransactionAllow.READONLY,
		family="OWSI",
		beanParent="webscript.owsi.remote")
public class SearchNodeWebScript extends AbstractNodeListWebScript<SEARCH_NODE_SERVICE> {

	@Autowired
	private NodeSearchRemoteService nodeSearchService;

	@Override
	protected List<RepositoryNode> execute(SEARCH_NODE_SERVICE parameter) {
		return nodeSearchService.search(
				Objects.requireNonNull(parameter.searchParameters, "SearchParameters"));
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(SEARCH_NODE_SERVICE.class);
	}

}
