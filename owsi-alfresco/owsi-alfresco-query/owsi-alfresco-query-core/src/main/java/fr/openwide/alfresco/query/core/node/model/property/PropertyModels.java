package fr.openwide.alfresco.query.core.node.model.property;

import fr.openwide.alfresco.query.core.node.model.TypeModel;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public final class PropertyModels {

	public static TextPropertyModel newText(TypeModel type, String namespace, String name) {
		return new TextPropertyModel(type, NameReference.create(namespace, name));
	}

	public static DateTimePropertyModel newDateTime(TypeModel type, String namespace, String name) {
		return new DateTimePropertyModel(type, NameReference.create(namespace, name));
	}

	public static LongPropertyModel newLong(TypeModel type, String namespace, String name) {
		return new LongPropertyModel(type, NameReference.create(namespace, name));
	}

	public static BooleanPropertyModel newBoolean(TypeModel type, String namespace, String name) {
		return new BooleanPropertyModel(type, NameReference.create(namespace, name));
	}

	public static ContentPropertyModel newContent(TypeModel type, String namespace, String name) {
		return new ContentPropertyModel(type, NameReference.create(namespace, name));
	}

	public static RefPropertyModel newRef(TypeModel type, String namespace, String name) {
		return new RefPropertyModel(type, NameReference.create(namespace, name));
	}

}
