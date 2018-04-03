package fr.openwide.alfresco.component.model.node.model.embed;

import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class PropertiesNodeScope {

	private final NodeScopeBuilder builder;
	private final NodeScope scope;
	
	public PropertiesNodeScope(NodeScopeBuilder builder) {
		this.builder = builder;
		this.scope = builder.getScope();
	}
	
	public NodeScopeBuilder name() {
		return set(CmModel.object.name);
	}
	public NodeScopeBuilder title() {
		return set(CmModel.titled.title);
	}
	public NodeScopeBuilder description() {
		return set(CmModel.titled.description);
	}

	public NodeScopeBuilder creator() {
		return set(CmModel.auditable.creator);
	}
	public NodeScopeBuilder created() {
		return set(CmModel.auditable.created);
	}
	public NodeScopeBuilder modifier() {
		return set(CmModel.auditable.modifier);
	}
	public NodeScopeBuilder modified() {
		return set(CmModel.auditable.modified);
	}

	public NodeScopeBuilder set(PropertyModel<?> propertyModel) {
		return set(propertyModel.getNameReference());
	}
	public NodeScopeBuilder set(NameReference property) {
		scope.getProperties().add(property);
		return builder;
	}
	public NodeScopeBuilder set(ContainerModel type) {
		for (PropertyModel<?> property : type.getProperties().values()) {
			set(property);
		}
		return builder;
	}

}
