package fr.openwide.alfresco.query.core.node.model.value;

import java.io.Serializable;

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

}
