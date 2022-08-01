package fr.openwide.alfresco.api.core.search.model;

import java.io.Serializable;

import org.alfresco.service.namespace.QName;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SortDefinition implements Serializable {
	
	private final QName property;
	private final boolean ascending;

	@JsonCreator
	public SortDefinition(
			@JsonProperty("property") QName property, 
			@JsonProperty("ascending") boolean ascending) {
		this.property = property;
		this.ascending = ascending;
	}

	public QName getProperty() {
		return property;
	}

	public boolean isAscending() {
		return ascending;
	}
	
}
