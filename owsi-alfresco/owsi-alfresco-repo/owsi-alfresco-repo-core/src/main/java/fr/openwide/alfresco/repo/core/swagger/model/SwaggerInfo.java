package fr.openwide.alfresco.repo.core.swagger.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SwaggerInfo {
	
	@JsonProperty public String title;
	@JsonProperty public String description;
	@JsonProperty public String version;

}
