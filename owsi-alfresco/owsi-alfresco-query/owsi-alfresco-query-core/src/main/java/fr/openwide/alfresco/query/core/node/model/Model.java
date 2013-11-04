package fr.openwide.alfresco.query.core.node.model;

import fr.openwide.alfresco.query.core.node.model.value.NameReference;

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

	public NameReference getNameReference() {
		return nameReference;
	}

}
