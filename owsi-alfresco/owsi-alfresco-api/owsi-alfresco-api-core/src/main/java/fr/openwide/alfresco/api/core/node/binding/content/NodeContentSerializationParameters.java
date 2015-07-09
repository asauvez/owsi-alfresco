package fr.openwide.alfresco.api.core.node.binding.content;

import java.util.HashMap;
import java.util.Map;

import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class NodeContentSerializationParameters {
	
	private Map<NameReference, NodeContentSerializer<?>> serializersByProperties = new HashMap<>();
	
	public Map<NameReference, NodeContentSerializer<?>> getSerializersByProperties() {
		return serializersByProperties;
	}

	@Override
	public NodeContentSerializationParameters clone() {
		NodeContentSerializationParameters parameters = new NodeContentSerializationParameters();
		parameters.getSerializersByProperties().putAll(this.getSerializersByProperties());
		return parameters;
	}
}
