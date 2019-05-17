package fr.openwide.alfresco.repo.core.swagger.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_EMPTY)
public class SwaggerWS {

	@JsonProperty public String summary;
	@JsonProperty public String description;
	@JsonProperty public String operationId;
	@JsonProperty public String[] tags;
	
	@JsonProperty public List<String> produces = new ArrayList<>();
	@JsonProperty public List<SwaggerParameterModel> parameters = new ArrayList<>();
	@JsonProperty public Map<String, SwaggerResponseModel> responses = new TreeMap<>();
}
