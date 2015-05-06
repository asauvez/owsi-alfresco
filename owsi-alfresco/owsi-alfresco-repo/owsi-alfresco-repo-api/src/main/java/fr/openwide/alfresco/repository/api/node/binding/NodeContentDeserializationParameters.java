package fr.openwide.alfresco.repository.api.node.binding;

import java.util.HashMap;
import java.util.Map;

import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class NodeContentDeserializationParameters {
	
	private Map<NameReference, NodeContentDeserializer<?>> deserializersByProperties = new HashMap<>();
	
	public Map<NameReference, NodeContentDeserializer<?>> getDeserializersByProperties() {
		return deserializersByProperties;
	}
	
	@Override
	public NodeContentDeserializationParameters clone() {
		NodeContentDeserializationParameters parameters = new NodeContentDeserializationParameters();
		parameters.getDeserializersByProperties().putAll(this.getDeserializersByProperties());
		return parameters;
	}
}
