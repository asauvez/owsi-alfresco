package fr.openwide.alfresco.query.repo.mapper;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

public class ObjectMapperProvider {

	private ObjectMapper objectMapper;

	public ObjectMapperProvider() {
		this.objectMapper = new ObjectMapper();
		this.objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
		this.objectMapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
	}

	public ObjectMapper getMapper() {
		return objectMapper;
	}

}
