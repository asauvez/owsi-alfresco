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

	private final String reference;

	private NodeReference(String reference) {
		this.reference = reference;
	}

	public static NodeReference create(String reference) {
		return new NodeReference(reference);
	}
	public static NodeReference create(StoreReference storeReference, String uuid) {
		return new NodeReference(storeReference + "/" + uuid);
	}

	public StoreReference getStoreReference() {
		Matcher matcher = PATTERN.matcher(reference);
		matcher.matches();
		return StoreReference.create(matcher.group(1));
	}
	public UUID getUuid() {
		Matcher matcher = PATTERN.matcher(reference);
		matcher.matches();
		return UUID.fromString(matcher.group(4));
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
