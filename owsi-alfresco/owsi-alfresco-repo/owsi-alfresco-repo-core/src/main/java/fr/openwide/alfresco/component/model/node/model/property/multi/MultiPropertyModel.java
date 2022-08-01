package fr.openwide.alfresco.component.model.node.model.property.multi;

import java.io.Serializable;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.component.model.node.model.constraint.ConstraintException;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;

public abstract class MultiPropertyModel<C extends Serializable> extends PropertyModel<C> {

	public MultiPropertyModel(ContainerModel type, QName qName) {
		super(type, qName);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void validateType(Serializable value) {
		if (value != null) {
			if (! (value instanceof Iterable)) {
				throw new ConstraintException(getQName() + ": Value of type " + value.getClass().getName() + " instead of Iterable.");
			}
			for (Serializable item : (Iterable<Serializable>) value) {
				super.validateType(item);
			}
		}
	}
}
