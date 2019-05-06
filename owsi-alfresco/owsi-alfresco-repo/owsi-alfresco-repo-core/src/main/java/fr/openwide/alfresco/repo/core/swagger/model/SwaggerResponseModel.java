package fr.openwide.alfresco.repo.core.swagger.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_EMPTY)
public class SwaggerResponseModel {

	@JsonProperty public String description;
	@JsonProperty public SwaggerSchema schema;
	
}
