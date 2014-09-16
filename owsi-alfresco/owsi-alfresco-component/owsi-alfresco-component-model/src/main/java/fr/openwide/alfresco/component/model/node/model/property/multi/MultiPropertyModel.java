package fr.openwide.alfresco.component.model.node.model.property.multi;

import java.io.Serializable;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.component.model.node.model.constraint.ConstraintException;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public abstract class MultiPropertyModel<C extends Serializable> extends PropertyModel<C> {

	public MultiPropertyModel(ContainerModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void validateType(Serializable value) {
		if (value != null && ! (value instanceof Iterable)) {
			throw new ConstraintException(getNameReference() + ": Value of type " + value.getClass().getName() + " instead of Iterable.");
		}
		for (Serializable item : (Iterable<Serializable>) value) {
			super.validateType(item);
		}
	}
}
