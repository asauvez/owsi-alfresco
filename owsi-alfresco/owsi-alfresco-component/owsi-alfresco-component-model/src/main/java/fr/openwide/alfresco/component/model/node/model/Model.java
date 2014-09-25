package fr.openwide.alfresco.component.model.node.model;

import java.util.Objects;

import fr.openwide.alfresco.repository.api.remote.model.NameReference;


public abstract class Model {

	protected final NameReference nameReference;

	public Model(NameReference nameReference) {
		this.nameReference = nameReference;
	}

	public String toLucene() {
		return nameReference.getNamespace() + "\\:" + nameReference.getName();
	}

	@Override
	public String toString() {
		return nameReference.toString();
	}
	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (object == this) {
			return true;
		}
		if (object.getClass() == this.getClass()) {
			Model other = (Model) object;
			return Objects.equals(getNameReference(), other.getNameReference());
		}
		return false;
	}
	@Override
	public int hashCode() {
		return getNameReference().hashCode();
	}

	public NameReference getNameReference() {
		return nameReference;
	}

}
