package fr.openwide.alfresco.component.model.node.model.property;

import java.io.Serializable;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.component.model.node.model.Model;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public abstract class PropertyModel<C extends Serializable> extends Model {

	private final ContainerModel type;

	public PropertyModel(ContainerModel type, NameReference nameReference) {
		super(nameReference);
		this.type = type;
		type.addProperty(this);
	}

	@Override
	public String toLucene() {
		return "@" + super.toLucene();
	}

	public abstract Class<C> getValueClass();

	public ContainerModel getType() {
		return type;
	}

}
