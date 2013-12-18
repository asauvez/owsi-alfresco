package fr.openwide.alfresco.query.core.node.model.property;

import fr.openwide.alfresco.query.core.node.model.ContainerModel;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public final class PropertyModels {

	public static TextPropertyModel newText(ContainerModel type, String namespace, String name) {
		return new TextPropertyModel(type, NameReference.create(namespace, name));
	}

	public static DateTimePropertyModel newDateTime(ContainerModel type, String namespace, String name) {
		return new DateTimePropertyModel(type, NameReference.create(namespace, name));
	}

	public static LongPropertyModel newLong(ContainerModel type, String namespace, String name) {
		return new LongPropertyModel(type, NameReference.create(namespace, name));
	}

	public static BooleanPropertyModel newBoolean(ContainerModel type, String namespace, String name) {
		return new BooleanPropertyModel(type, NameReference.create(namespace, name));
	}

	public static ContentPropertyModel newContent(ContainerModel type, String namespace, String name) {
		return new ContentPropertyModel(type, NameReference.create(namespace, name));
	}

	public static RefPropertyModel newRef(ContainerModel type, String namespace, String name) {
		return new RefPropertyModel(type, NameReference.create(namespace, name));
	}

}
