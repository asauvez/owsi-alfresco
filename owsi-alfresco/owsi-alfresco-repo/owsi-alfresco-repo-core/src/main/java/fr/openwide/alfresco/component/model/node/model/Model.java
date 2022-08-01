package fr.openwide.alfresco.component.model.node.model;

import java.util.Objects;

import org.alfresco.service.namespace.QName;

public abstract class Model {

	protected final QName qName;

	public Model(QName qName) {
		this.qName = qName;
	}

	public String toLucene() {
		String[] split = qName.toPrefixString().split(":");
		return split[0].replace("-", "\\-") 
			+ "\\:" 
			+ split[1].replace("-", "\\-");
	}

	@Override
	public String toString() {
		return getQName().toPrefixString();
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
			return Objects.equals(getQName(), other.getQName());
		}
		return false;
	}
	@Override
	public int hashCode() {
		return getQName().hashCode();
	}

	public QName getQName() {
		return qName;
	}
}
