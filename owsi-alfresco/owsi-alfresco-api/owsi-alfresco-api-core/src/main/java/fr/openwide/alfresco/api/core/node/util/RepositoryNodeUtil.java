package fr.openwide.alfresco.api.core.node.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

public class RepositoryNodeUtil {

	private static ThreadLocal<Boolean> readOnly = new ThreadLocal<>();
	
	public static <E> Set<E> init(Set<E> set) {
		if (set instanceof LinkedHashSet) return set;
		if (set != null && set != Collections.emptySet()) {
			throw new IllegalStateException("Vous devez rajouter @JsonDeserialize(as=LinkedHashSet.class) sur ce champs");
		};
		
		if (readOnly.get() != null) {
			return Collections.emptySet();
		} else {
			return new LinkedHashSet<>();
		}
	}
	public static <K, V> Map<K, V> init(Map<K, V> map) {
		if (map instanceof LinkedHashMap) return map;
		if (map != null && map != Collections.emptyMap()) {
			throw new IllegalStateException("Vous devez rajouter @JsonDeserialize(as=LinkedHashMap.class) sur ce champs");
		};
		
		if (readOnly.get() != null) {
			return Collections.emptyMap();
		} else {
			return new LinkedHashMap<>();
		}
	}

	public static <K, V>  Map<K, V> set(Map<K, V> map, K key, V value) {
		if (!(map instanceof LinkedHashMap)) {
			map = new LinkedHashMap<>();
		}
		map.put(key, value);
		return map;
	}

	public static void runInReadOnly(Runnable runnable) {
		readOnly.set(Boolean.TRUE);
		try {
			runnable.run();
		} finally {
			readOnly.remove();
		}
	}
	public static <V> V runInReadOnly(Callable<V> callable) {
		Boolean oldValue = readOnly.get();
		try {
			readOnly.set(Boolean.TRUE);
			
			return callable.call();
		} catch (Exception e) {
			throw (e instanceof RuntimeException) ? (RuntimeException) e : new RuntimeException(e);
		} finally {
			if (oldValue != null) {
				readOnly.set(oldValue);
			} else {
				readOnly.remove();
			}
		}
	}
	
	private RepositoryNodeUtil() {}
}
