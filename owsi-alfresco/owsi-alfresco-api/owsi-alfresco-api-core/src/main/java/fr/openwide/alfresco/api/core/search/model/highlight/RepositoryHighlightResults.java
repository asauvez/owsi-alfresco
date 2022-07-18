package fr.openwide.alfresco.api.core.search.model.highlight;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Joiner;

public class RepositoryHighlightResults implements Serializable {
	
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
}
