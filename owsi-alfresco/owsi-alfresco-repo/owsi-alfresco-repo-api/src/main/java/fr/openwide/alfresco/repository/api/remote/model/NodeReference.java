package fr.openwide.alfresco.repository.api.remote.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Objects;

public class NodeReference implements Serializable {

	private static final long serialVersionUID = -1502875087111354789L;

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
	public boolean equals(Object obj) {
        if (obj instanceof NodeReference) {
            return Objects.equal(getReference(), ((NodeReference) obj).getReference());
        }
        return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getReference());
	}
}
