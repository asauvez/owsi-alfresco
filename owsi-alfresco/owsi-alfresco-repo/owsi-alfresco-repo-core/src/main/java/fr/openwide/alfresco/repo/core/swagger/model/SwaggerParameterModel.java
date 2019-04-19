package fr.openwide.alfresco.repo.core.swagger.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_EMPTY)
public class SwaggerParameterModel {

	@JsonProperty public String name;
	@JsonProperty public String in;
	@JsonProperty public String description;
	@JsonProperty public Boolean required;
	@JsonProperty public String type;
	@JsonProperty public SwaggerSchema schema;
	
}
