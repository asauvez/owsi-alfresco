package fr.openwide.alfresco.repository.api.node.binding;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Optional;

import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class NodeContentSerializationParameters {
	
	public static final int DEFAULT_COMPRESSION_LEVEL = 0; // sans compression
	
	private Optional<Integer> compressionLevel = Optional.absent();
	private Map<NameReference, NodeContentSerializer<?>> serializersByProperties = new HashMap<>();
	
	public Optional<Integer> getCompressionLevel() {
		return compressionLevel;
	}
	public void setCompressionLevel(Optional<Integer> compressionLevel) {
		this.compressionLevel = compressionLevel;
	}
	
	public Map<NameReference, NodeContentSerializer<?>> getSerializersByProperties() {
		return serializersByProperties;
	}

	@Override
	public NodeContentSerializationParameters clone() {
		NodeContentSerializationParameters parameters = new NodeContentSerializationParameters();
		parameters.setCompressionLevel(this.getCompressionLevel());
		parameters.getSerializersByProperties().putAll(this.getSerializersByProperties());
		return parameters;
	}
}
