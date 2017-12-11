package fr.openwide.alfresco.api.core.remote.model;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonValue;

public class StoreReference implements Serializable {

	private static final long serialVersionUID = -1502875087111354789L;

	public static final StoreReference STORE_REF_WORKSPACE_SPACESSTORE = new StoreReference("workspace://SpacesStore");
	public static final StoreReference STORE_REF_ARCHIVE_SPACESSTORE = new StoreReference("archive://SpacesStore");

	private final String reference;

	private StoreReference(String reference) {
		this.reference = reference;
	}

	public static StoreReference create(String reference) {
		return new StoreReference(reference);
	}
	public static StoreReference create(String protocol, String identifier) {
		return create(protocol + "://" + identifier);
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
		if (object instanceof StoreReference) {
			StoreReference other = (StoreReference) object;
			return Objects.equals(getReference(), other.getReference());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getReference());
	}

}
