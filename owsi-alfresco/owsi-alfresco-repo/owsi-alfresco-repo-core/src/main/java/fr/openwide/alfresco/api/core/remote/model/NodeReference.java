package fr.openwide.alfresco.api.core.remote.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonValue;

public class NodeReference implements Serializable {

	private static final long serialVersionUID = -1502875087111354789L;

	public static final Pattern PATTERN = Pattern.compile("((.+)://(.+))/(.+)");
	public static final int PATTERN_STORE_REFERENCE = 1;
	public static final int PATTERN_WORKSPACE = 2;
	public static final int PATTERN_STORE = 3;
	public static final int PATTERN_UUID = 4;

	private final String reference;

	private NodeReference(String reference) {
		this.reference = reference;
	}

	public static NodeReference create(String reference) {
		return new NodeReference(reference);
	}
	public static NodeReference create(StoreReference storeReference, String uuid) {
		return create(storeReference + "/" + uuid);
	}
	public static NodeReference create(String protocol, String identifier, String uuid) {
		return create(protocol + "://" + identifier + "/" + uuid);
	}

	public StoreReference getStoreReference() {
		Matcher matcher = PATTERN.matcher(reference);
		matcher.matches();
		return StoreReference.create(matcher.group(PATTERN_STORE_REFERENCE));
	}
	public UUID getUuid() {
		Matcher matcher = PATTERN.matcher(reference);
		matcher.matches();
		return UUID.fromString(matcher.group(PATTERN_UUID));
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
