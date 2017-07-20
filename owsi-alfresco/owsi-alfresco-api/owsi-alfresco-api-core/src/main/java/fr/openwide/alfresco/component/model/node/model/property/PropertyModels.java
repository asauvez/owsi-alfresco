package fr.openwide.alfresco.component.model.node.model.property;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.remote.model.NamespaceReference;
import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.component.model.node.model.constraint.PropertyConstraint;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiBooleanPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiDatePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiDateTimePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiDoublePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiFloatPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiIntegerPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiLongPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiNameReferencePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiNodeReferencePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiTextPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.BooleanPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.ContentPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.DatePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.DateTimePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.DoublePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.EnumTextPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.FloatPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.IntegerPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.LocalePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.LongPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.NameReferencePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.NodeReferencePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;

public final class PropertyModels {

	public static <M extends PropertyModel<?>> M addConstraints(M propertyModel, PropertyConstraint ... constraints) {
		for (PropertyConstraint constraint : constraints) {
			propertyModel.add(constraint);
		}
		return propertyModel;
	}
	
	public static TextPropertyModel newText(ContainerModel type, NamespaceReference namespace, String name, PropertyConstraint ... constraints) {
		return addConstraints(new TextPropertyModel(type, NameReference.create(namespace, name)), constraints);
	}
	public static MultiTextPropertyModel newMultiText(ContainerModel type, NamespaceReference namespace, String name) {
		return new MultiTextPropertyModel(type, NameReference.create(namespace, name));
	}
	public static <E extends Enum<E>> EnumTextPropertyModel<E> newTextEnum(ContainerModel type, NamespaceReference namespace, String name, 
			Class<E> enumClass, PropertyConstraint ... constraints) {
		return addConstraints(new EnumTextPropertyModel<E>(type, NameReference.create(namespace, name), enumClass), constraints);
	}

	public static DatePropertyModel newDate(ContainerModel type, NamespaceReference namespace, String name) {
		return new DatePropertyModel(type, NameReference.create(namespace, name));
	}
	public static MultiDatePropertyModel newMultiDate(ContainerModel type, NamespaceReference namespace, String name) {
		return new MultiDatePropertyModel(type, NameReference.create(namespace, name));
	}
	public static DateTimePropertyModel newDateTime(ContainerModel type, NamespaceReference namespace, String name, PropertyConstraint ... constraints) {
		return addConstraints(new DateTimePropertyModel(type, NameReference.create(namespace, name)), constraints);
	}
	public static MultiDateTimePropertyModel newMultiDateTime(ContainerModel type, NamespaceReference namespace, String name) {
		return new MultiDateTimePropertyModel(type, NameReference.create(namespace, name));
	}
	public static LocalePropertyModel newLocale(ContainerModel type, NamespaceReference namespace, String name) {
		return new LocalePropertyModel(type, NameReference.create(namespace, name));
	}

	public static IntegerPropertyModel newInteger(ContainerModel type, NamespaceReference namespace, String name) {
		return new IntegerPropertyModel(type, NameReference.create(namespace, name));
	}
	public static MultiIntegerPropertyModel newMultiInteger(ContainerModel type, NamespaceReference namespace, String name) {
		return new MultiIntegerPropertyModel(type, NameReference.create(namespace, name));
	}
	public static LongPropertyModel newLong(ContainerModel type, NamespaceReference namespace, String name) {
		return new LongPropertyModel(type, NameReference.create(namespace, name));
	}
	public static LongPropertyModel newLong(ContainerModel type, NamespaceReference namespace, String name, PropertyConstraint ... constraints) {
		return addConstraints(new LongPropertyModel(type, NameReference.create(namespace, name)), constraints);
	}
	public static MultiLongPropertyModel newMultiLong(ContainerModel type, NamespaceReference namespace, String name) {
		return new MultiLongPropertyModel(type, NameReference.create(namespace, name));
	}
	public static FloatPropertyModel newFloat(ContainerModel type, NamespaceReference namespace, String name) {
		return new FloatPropertyModel(type, NameReference.create(namespace, name));
	}
	public static MultiFloatPropertyModel newMultiFloat(ContainerModel type, NamespaceReference namespace, String name) {
		return new MultiFloatPropertyModel(type, NameReference.create(namespace, name));
	}
	public static DoublePropertyModel newDouble(ContainerModel type, NamespaceReference namespace, String name) {
		return new DoublePropertyModel(type, NameReference.create(namespace, name));
	}
	public static MultiDoublePropertyModel newMultiDouble(ContainerModel type, NamespaceReference namespace, String name) {
		return new MultiDoublePropertyModel(type, NameReference.create(namespace, name));
	}

	public static BooleanPropertyModel newBoolean(ContainerModel type, NamespaceReference namespace, String name) {
		return new BooleanPropertyModel(type, NameReference.create(namespace, name));
	}
	public static MultiBooleanPropertyModel newMultiBoolean(ContainerModel type, NamespaceReference namespace, String name) {
		return new MultiBooleanPropertyModel(type, NameReference.create(namespace, name));
	}

	public static ContentPropertyModel newContent(ContainerModel type, NamespaceReference namespace, String name) {
		return new ContentPropertyModel(type, NameReference.create(namespace, name));
	}

	public static NodeReferencePropertyModel newNodeReference(ContainerModel type, NamespaceReference namespace, String name) {
		return new NodeReferencePropertyModel(type, NameReference.create(namespace, name));
	}
	public static MultiNodeReferencePropertyModel newMultiNodeReference(ContainerModel type, NamespaceReference namespace, String name) {
		return new MultiNodeReferencePropertyModel(type, NameReference.create(namespace, name));
	}
	public static NameReferencePropertyModel newNameReference(ContainerModel type, NamespaceReference namespace, String name) {
		return new NameReferencePropertyModel(type, NameReference.create(namespace, name));
	}
	public static MultiNameReferencePropertyModel newMultiNameReference(ContainerModel type, NamespaceReference namespace, String name) {
		return new MultiNameReferencePropertyModel(type, NameReference.create(namespace, name));
	}

}
