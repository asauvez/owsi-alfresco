package fr.openwide.alfresco.component.model.node.model;

import java.util.ArrayList;
import java.util.List;

import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public abstract class ContainerModel extends Model {

	private List<PropertyModel<?>> properties = new ArrayList<PropertyModel<?>>();
	
	public ContainerModel(NameReference nameReference) {
		super(nameReference);
	}

	public List<PropertyModel<?>> getProperties() {
		return properties;
	}
}
