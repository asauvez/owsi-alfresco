package fr.openwide.alfresco.query.core.node.model.value;

import java.io.Serializable;

import com.google.common.base.Objects;

public class NameReference implements Serializable {

	private static final long serialVersionUID = -1010067410648716413L;

	private final String namespace;
	private final String name;

	private NameReference(String namespace, String name) {
		this.namespace = namespace;
		this.name = name;
	}

	public static NameReference create(String namespace, String name) {
		return new NameReference(namespace, name);
	}

	public String getNamespace() {
		return namespace;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return namespace + ":" + name;
	}

	@Override
	public boolean equals(Object obj) {
        if (obj instanceof NameReference) {
            return getNamespace().equals(((NameReference) obj).getNamespace())
            	&& getName().equals(     ((NameReference) obj).getName());
        }
        return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(getNamespace(), getName());
	}
}
