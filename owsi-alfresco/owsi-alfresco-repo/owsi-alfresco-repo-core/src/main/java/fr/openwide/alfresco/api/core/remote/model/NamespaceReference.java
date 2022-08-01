package fr.openwide.alfresco.api.core.remote.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.NamespaceServiceMemoryImpl;
import org.alfresco.service.namespace.QName;

public class NamespaceReference implements Serializable {

	private static NamespaceService namespaceService = new NamespaceServiceMemoryImpl();
	
	private final String prefix;
	private final String uri;

	private NamespaceReference(String prefix, String uri) {
		this.prefix = prefix;
		this.uri = uri;
	}
	public static NamespaceReference create(String prefix, String uri) {
		NamespaceReference namespaceReference = new NamespaceReference(prefix, uri);
		namespaceService.registerNamespace(prefix, uri);
		return namespaceReference;
	}
	
	public QName createQName(String localName) {
		return QName.createQName(this.prefix, localName, namespaceService);
	}

	public String getPrefix() {
		return prefix;
	}
	public String getUri() {
		return uri;
	}

	public static String getUriByPrefix(String prefix) {
		String uri = namespaceService.getNamespaceURI(prefix);
		if (uri == null) {
			throw new IllegalArgumentException("Unknown prefix " + prefix);
		}
		return uri;
	}
	public static String getPrefixByUri(String uri) {
		Collection<String> prefix = namespaceService.getPrefixes(uri);
		if (prefix == null || prefix.isEmpty()) {
			throw new IllegalArgumentException("Unknown uri " + uri);
		}
		return prefix.iterator().next();
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
