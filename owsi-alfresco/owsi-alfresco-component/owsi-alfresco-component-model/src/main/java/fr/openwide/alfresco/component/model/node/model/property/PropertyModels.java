package fr.openwide.alfresco.component.model.node.model.property;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public final class PropertyModels {

	public static TextPropertyModel newText(ContainerModel type, String namespace, String name) {
		return new TextPropertyModel(type, NameReference.create(namespace, name));
	}

	public static DatePropertyModel newDate(ContainerModel type, String namespace, String name) {
		return new DatePropertyModel(type, NameReference.create(namespace, name));
	}
	public static DateTimePropertyModel newDateTime(ContainerModel type, String namespace, String name) {
		return new DateTimePropertyModel(type, NameReference.create(namespace, name));
	}

	public static IntegerPropertyModel newInteger(ContainerModel type, String namespace, String name) {
		return new IntegerPropertyModel(type, NameReference.create(namespace, name));
	}
	public static LongPropertyModel newLong(ContainerModel type, String namespace, String name) {
		return new LongPropertyModel(type, NameReference.create(namespace, name));
	}
	public static FloatPropertyModel newFloat(ContainerModel type, String namespace, String name) {
		return new FloatPropertyModel(type, NameReference.create(namespace, name));
	}
	public static DoublePropertyModel newDouble(ContainerModel type, String namespace, String name) {
		return new DoublePropertyModel(type, NameReference.create(namespace, name));
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
	public static NameReferencePropertyModel newNameReference(ContainerModel type, String namespace, String name) {
		return new NameReferencePropertyModel(type, NameReference.create(namespace, name));
	}

}
