package fr.openwide.alfresco.repository.remote.framework.service.impl;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ObjectMapperProvider implements FactoryBean<ObjectMapper>, InitializingBean {

	private ObjectMapper objectMapper;

	@Override
	public void afterPropertiesSet() {
		objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		objectMapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false);
	}

	@Override
	public Class<?> getObjectType() {
		return ObjectMapper.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public ObjectMapper getObject() {
		return objectMapper;
	}

}
