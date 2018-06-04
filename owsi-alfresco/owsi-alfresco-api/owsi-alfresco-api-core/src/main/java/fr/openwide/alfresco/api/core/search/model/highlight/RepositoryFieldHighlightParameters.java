
package fr.openwide.alfresco.api.core.search.model.highlight;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class RepositoryFieldHighlightParameters extends RepositoryHighlightParameters {
	private final NameReference field;

    @JsonCreator
	public RepositoryFieldHighlightParameters(@JsonProperty("field") NameReference field,
			@JsonProperty("snippetCount") Integer snippetCount, @JsonProperty("fragmentSize") Integer fragmentSize,
			@JsonProperty("mergeContiguous") Boolean mergeContiguous, @JsonProperty("prefix") String prefix,
			@JsonProperty("postfix") String postfix) {
		super(snippetCount, fragmentSize, mergeContiguous, prefix, postfix);
		this.field = field;
	}
	
	public NameReference getField() {
		return field;
	}
}
