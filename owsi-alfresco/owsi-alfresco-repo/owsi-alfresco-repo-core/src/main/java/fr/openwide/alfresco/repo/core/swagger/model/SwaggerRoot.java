package fr.openwide.alfresco.repo.core.swagger.model;

import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SwaggerRoot {

	@JsonProperty public String swagger = "2.0";
	@JsonProperty public String host;
	@JsonProperty public String basePath;
	@JsonProperty public String[] schemes;
	
	@JsonProperty public String title;
	@JsonProperty public String description;
	@JsonProperty public String version;
	
	@JsonProperty public Map<String, Map<String, SwaggerWS>> paths = new TreeMap<>();
}
