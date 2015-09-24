package fr.openwide.alfresco.api.core.remote.model;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class NamespaceReference implements Serializable {

	private static Map<String, String> uriByPrefix = new ConcurrentHashMap<>(); 
	
	private final String prefix;
	private final String uri;

	private NamespaceReference(String prefix, String uri) {
		this.prefix = prefix;
		this.uri = uri;
	}
	public static NamespaceReference create(String prefix, String uri) {
		NamespaceReference namespaceReference = new NamespaceReference(prefix, uri);
		uriByPrefix.put(prefix, uri);
		return namespaceReference;
	}

	public String getPrefix() {
		return prefix;
	}
	public String getUri() {
		return uri;
	}

	public static String getUriByPrefix(String prefix) {
		String uri = uriByPrefix.get(prefix);
		if (uri == null) {
			throw new IllegalArgumentException("Unknown prefix " + prefix);
		}
		return uri;
	}

	@Override
	public String toString() {
		return getPrefix();
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (object == this) {
			return true;
		}
		if (object instanceof NamespaceReference) {
			NamespaceReference other = (NamespaceReference) object;
			return Objects.equals(getPrefix(), other.getPrefix());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getPrefix());
	}

}
