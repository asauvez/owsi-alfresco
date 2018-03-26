package fr.openwide.alfresco.api.core.search.model.highlight;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;

import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.search.service.NodeSearchRemoteService;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;

public class RepositoryHighlightResults implements Serializable {
	
	private static final NameReference HIGHLIGHTING = NameReference.create(NodeSearchRemoteService.class.getName(), "highlighting");
	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	private List<RepositoryHighlightResult> results;
	
	@JsonCreator
	public RepositoryHighlightResults(@JsonProperty("results") List<RepositoryHighlightResult> results) {
		super();
		this.results = results;
	}
	
	public List<RepositoryHighlightResult> getResults() {
		return results;
	}

	public String toCommaSeparated() {
		return toString("",  "", ", ");
	}
	public String toHtmlUl() {
		return toString("<ul><li>",  "</li></ul>", "</li><li>");
	}
	public String toString(String prefix, String suffix, String separator) {
		if (results.size() == 0) return "";
		return prefix + Joiner.on(separator).join(results.stream()
			.flatMap(result -> result.getExtracts().stream())
			.collect(Collectors.toList())) 
			+ suffix;
	}


	public void storeInNode(RepositoryNode node) {
		try {
			String s = objectMapper.writeValueAsString(this);
			node.getExtensions().put(HIGHLIGHTING, s);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException(e);
		}
	}
	public static RepositoryHighlightResults extractFromNode(RepositoryNode node) {
		String s = (String) node.getExtension(HIGHLIGHTING);
		try {
			RepositoryHighlightResults highlightResults = objectMapper.readValue(s, RepositoryHighlightResults.class);
			return (highlightResults != null) ? highlightResults : new RepositoryHighlightResults(Collections.emptyList());
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public static RepositoryHighlightResults extractFromNode(BusinessNode node) {
		return extractFromNode(node.getRepositoryNode());
	}
}
