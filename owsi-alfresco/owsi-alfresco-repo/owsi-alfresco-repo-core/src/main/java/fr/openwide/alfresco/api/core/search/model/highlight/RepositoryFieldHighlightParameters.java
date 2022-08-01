
package fr.openwide.alfresco.api.core.search.model.highlight;

import org.alfresco.service.namespace.QName;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RepositoryFieldHighlightParameters extends RepositoryHighlightParameters {
	private final QName field;

    @JsonCreator
	public RepositoryFieldHighlightParameters(@JsonProperty("field") QName field,
			@JsonProperty("snippetCount") Integer snippetCount, @JsonProperty("fragmentSize") Integer fragmentSize,
			@JsonProperty("mergeContiguous") Boolean mergeContiguous, @JsonProperty("prefix") String prefix,
			@JsonProperty("postfix") String postfix) {
		super(snippetCount, fragmentSize, mergeContiguous, prefix, postfix);
		this.field = field;
	}
	
	public QName getField() {
		return field;
	}
}
