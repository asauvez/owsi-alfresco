package fr.openwide.alfresco.repo.core.swagger.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.types.ObjectSchema;

@JsonInclude(Include.NON_EMPTY)
public class SwaggerSchema {

	@JsonProperty public String type;
	@JsonProperty public List<String> required = new ArrayList<>();
	@JsonProperty public Map<String, SwaggerSchema> properties = new TreeMap<>();
	
	public SwaggerSchema(JsonSchema schema) {
		type = schema.getType().value();
		
		if (schema instanceof ObjectSchema) {
			Map<String, JsonSchema> properties = ((ObjectSchema) schema).getProperties();
			for (Entry<String, JsonSchema> property : properties.entrySet()) {
				this.properties.put(
						property.getKey(), 
						new SwaggerSchema(property.getValue()));
				if (property.getValue().getRequired() != null && property.getValue().getRequired()) {
					this.required.add(property.getKey());
				}
			}
		}
	}
	
}
