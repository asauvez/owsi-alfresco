package fr.openwide.alfresco.component.model.node.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public abstract class ContainerModel extends Model {

	private Map<NameReference, PropertyModel<?>> properties = new HashMap<NameReference, PropertyModel<?>>();

	private Map<NameReference, AspectModel> mandatoryAspects = new HashMap<NameReference, AspectModel>();

	public ContainerModel(NameReference nameReference) {
		super(nameReference);
	}

	public Map<NameReference, PropertyModel<?>> getProperties() {
		return Collections.unmodifiableMap(properties);
	}
	public PropertyModel<?> getProperty(NameReference nameReference) {
		return properties.get(nameReference);
	}
	public void addProperty(PropertyModel<?> property) {
		properties.put(property.getNameReference(), property);
	}

	public Map<NameReference, AspectModel> getMandatoryAspects() {
		return Collections.unmodifiableMap(mandatoryAspects);
	}
	public <A extends AspectModel> A addMandatoryAspect(A aspect) {
		mandatoryAspects.put(aspect.getNameReference(), aspect);
		for (PropertyModel<?> property : aspect.getProperties().values()) {
			properties.put(property.getNameReference(), property);
		}
		return aspect;
	}
	
	/**
	 * Adds all properties and aspects from the source model to the destination.
	 */
	protected static <T extends ContainerModel> void copy(T source, T destination) {
		for (PropertyModel<?> property : source.getProperties().values()) {
			destination.addProperty(property);
		}
		for (AspectModel mandatoryAspect : source.getMandatoryAspects().values()) {
			destination.addMandatoryAspect(mandatoryAspect);
		}
	}
	
}
