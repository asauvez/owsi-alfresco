package fr.openwide.alfresco.repository.api.remote.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonValue;

public class NodeReference implements Serializable {

	private static final long serialVersionUID = -1502875087111354789L;

	public static final Pattern PATTERN = Pattern.compile("(.+)://(.+)/(.+)");

	private final String reference;

	private NodeReference(String reference) {
		this.reference = reference;
	}

	public static NodeReference create(String reference) {
		return new NodeReference(reference);
	}

	@JsonValue
	public String getReference() {
		return reference;
	}

	@Override
	public String toString() {
		return reference;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (object == this) {
			return true;
		}
		if (object instanceof NodeReference) {
			NodeReference other = (NodeReference) object;
			return Objects.equals(getReference(), other.getReference());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getReference());
	}

}
