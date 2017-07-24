package fr.openwide.alfresco.repo.module.classification.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.repo.dictionary.node.service.NodeModelRepositoryService;

public class ClassificationCache {
	
	private Map<String, NodeReference> cache = Collections.synchronizedMap(new LinkedHashMap<String, NodeReference>() {
		@Override
		protected boolean removeEldestEntry(Map.Entry<String,NodeReference> eldest) {
			return size() > maxSize;
		};
	});
	
	private int maxSize;

	public ClassificationCache(int maxSize) {
		this.maxSize = maxSize;
	}
	
	public Optional<NodeReference> get(NodeModelRepositoryService nodeModelService, String cacheKey, 
			Supplier<Optional<NodeReference>> ifNotInCache) {
		NodeReference result = cache.get(cacheKey);
		if (result == null) {
			Optional<NodeReference> opt = ifNotInCache.get();
			if (opt.isPresent()) {
				cache.put(cacheKey, result);
			}
			return opt;
		} else {
			// Vérifie juste que la node existe toujours
			if (! nodeModelService.exists(result)) {
				cache.remove(cacheKey);
				return Optional.empty();
			}
			return Optional.of(result);
		}
	}

	public NodeReference get(NodeModelRepositoryService nodeModelService, String cacheKey, 
			Supplier<Optional<NodeReference>> ifNotInCache,
			Supplier<NodeReference> ifNotExist) {
		Optional<NodeReference> result = get(nodeModelService, cacheKey, ifNotInCache);
		if (result.isPresent()) {
			return result.get();
		} else {
			NodeReference nodeReference = ifNotExist.get();
			cache.put(cacheKey, nodeReference);
			return nodeReference;
		}
	}

	public void put(String key, NodeReference nodeReference) {
		cache.put(key, nodeReference);
	}
	
	public void clear() {
		cache.clear();
	}
}
