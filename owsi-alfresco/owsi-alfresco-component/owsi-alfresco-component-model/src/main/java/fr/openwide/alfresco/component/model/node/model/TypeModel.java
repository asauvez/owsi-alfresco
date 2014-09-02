package fr.openwide.alfresco.component.model.node.model;

import java.util.ArrayList;
import java.util.List;

import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public abstract class TypeModel extends ContainerModel {

	private List<AspectModel> mandatoryAspects = new ArrayList<AspectModel>();

	public TypeModel(NameReference nameReference) {
		super(nameReference);
	}

	public List<AspectModel> getMandatoryAspects() {
		return mandatoryAspects;
	}
	public <A extends AspectModel> A addMandatoryAspect(A aspect) {
		mandatoryAspects.add(aspect);
		for (PropertyModel<?> property : aspect.getProperties()) {
			getProperties().add(property);
		}
		return aspect;
	}
}
