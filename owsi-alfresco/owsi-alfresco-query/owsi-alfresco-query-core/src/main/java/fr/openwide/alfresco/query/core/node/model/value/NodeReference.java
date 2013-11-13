package fr.openwide.alfresco.query.core.node.model.value;

import java.io.Serializable;
import java.util.Objects;

public class NodeReference implements Serializable {

	private static final long serialVersionUID = -1502875087111354789L;

	private final String reference;

	public NodeReference(String reference) {
		this.reference = reference;
	}

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
            return getReference().equals(((NodeReference) obj).getReference());
        }
        return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(getReference());
	}
}
