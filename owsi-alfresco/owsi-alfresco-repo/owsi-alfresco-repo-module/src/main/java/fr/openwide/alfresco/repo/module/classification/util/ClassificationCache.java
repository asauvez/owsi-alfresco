package fr.openwide.alfresco.repo.module.classification.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import org.alfresco.service.cmr.repository.NodeRef;

import fr.openwide.alfresco.repo.dictionary.node.service.NodeModelRepositoryService;

public class ClassificationCache {
	
	private Map<String, NodeRef> cache 
			= Collections.synchronizedMap(new LinkedHashMap<String, NodeRef>(16, 10.75f, true) {
		private static final long serialVersionUID = 3057991717141359392L;
		@Override
		protected boolean removeEldestEntry(Map.Entry<String, NodeRef> eldest) {
			return size() > maxSize;
		};
	});
	
	private int maxSize;

	public ClassificationCache(int maxSize) {
		this.maxSize = maxSize;
	}
	
	public Optional<NodeRef> get(NodeModelRepositoryService nodeModelService, String cacheKey, 
			Supplier<Optional<NodeRef>> ifNotInCache) {
		NodeRef result = cache.get(cacheKey);
		if (result == null) {
			Optional<NodeRef> opt = ifNotInCache.get();
			if (opt.isPresent()) {
				put(cacheKey, result);
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

	public NodeRef get(NodeModelRepositoryService nodeModelService, String cacheKey, 
			Supplier<Optional<NodeRef>> ifNotInCache,
			Supplier<NodeRef> ifNotExist) {
		Optional<NodeRef> result = get(nodeModelService, cacheKey, ifNotInCache);
		if (result.isPresent()) {
			return result.get();
		} else {
			NodeRef nodeRef = ifNotExist.get();
			put(cacheKey, nodeRef);
			return nodeRef;
		}
	}

	public void put(String key, NodeRef nodeRef) {
		if (maxSize > 0) {
			cache.put(key, nodeRef);
		}
	}
	
	public void clear() {
		cache.clear();
	}
}
