package fr.openwide.alfresco.api.core.remote.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

public class NameReference implements Serializable {

	private static final long serialVersionUID = -1010067410648716413L;

	private final String namespace;
	private final String name;

	private NameReference(String qname) {
		Iterator<String> it = Splitter.on(":").split(qname).iterator();
		namespace = it.next();
		name = it.next();
	}

	private NameReference(String namespace, String name) {
		Objects.requireNonNull(namespace, "namespace");
		Objects.requireNonNull(name, "name");
		this.namespace = namespace;
		this.name = name;
	}

	public static NameReference create(String namespace, String name) {
		return new NameReference(namespace, name);
	}
	public static NameReference create(String qname) {
		return new NameReference(qname);
	}

	public String getNamespace() {
		return namespace;
	}

	public String getName() {
		return name;
	}

	@JsonValue
	public String getFullName() {
		return Joiner.on(":").join(namespace, name); 
	}

	@Override
	public String toString() {
		return getFullName();
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (object == this) {
			return true;
		}
		if (object instanceof NameReference) {
			NameReference other = (NameReference) object;
			return Objects.equals(getNamespace(), other.getNamespace())
				&& Objects.equals(getName(), other.getName());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getNamespace(), getName());
	}

}
