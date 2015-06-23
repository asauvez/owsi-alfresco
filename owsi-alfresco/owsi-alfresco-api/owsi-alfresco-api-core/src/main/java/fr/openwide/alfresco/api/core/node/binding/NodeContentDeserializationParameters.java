package fr.openwide.alfresco.api.core.node.binding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeContentDeserializationParameters {
	
	private Map<List<Object>, NodeContentDeserializer<?>> deserializersByPath = new HashMap<>();
	
	public Map<List<Object>, NodeContentDeserializer<?>> getDeserializersByPath() {
		return deserializersByPath;
	}
	
	@Override
	public NodeContentDeserializationParameters clone() {
		NodeContentDeserializationParameters parameters = new NodeContentDeserializationParameters();
		parameters.getDeserializersByPath().putAll(this.getDeserializersByPath());
		return parameters;
	}
}
