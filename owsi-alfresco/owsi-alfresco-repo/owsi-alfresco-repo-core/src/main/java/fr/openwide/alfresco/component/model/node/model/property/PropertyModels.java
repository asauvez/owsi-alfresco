package fr.openwide.alfresco.component.model.node.model.property;

import fr.openwide.alfresco.api.core.remote.model.NamespaceReference;
import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.component.model.node.model.constraint.PropertyConstraint;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiAnyPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiAssociationRefPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiBooleanPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiChildAssociationRefPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiDatePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiDateTimePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiDoublePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiFloatPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiIntegerPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiLongPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiNodeRefPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiQNamePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiTextPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.AnyPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.AssociationRefPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.BooleanPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.ChildAssociationRefPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.ContentPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.DatePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.DateTimePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.DoublePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.EnumTextPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.FloatPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.IntegerPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.LocalePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.LongPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.NodeRefPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.QNamePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;

public final class PropertyModels {
	
	public static <M extends PropertyModel<?>> M addConstraints(M propertyModel, PropertyConstraint ... constraints) {
		for (PropertyConstraint constraint : constraints) {
			propertyModel.add(constraint);
		}
		return propertyModel;
	}
	
	public static TextPropertyModel newText(ContainerModel type, NamespaceReference namespace, String name, PropertyConstraint ... constraints) {
		return addConstraints(new TextPropertyModel(type, namespace.createQName(name)), constraints);
	}
	public static MultiTextPropertyModel newMultiText(ContainerModel type, NamespaceReference namespace, String name) {
		return new MultiTextPropertyModel(type, namespace.createQName(name));
	}
	public static <E extends Enum<E>> EnumTextPropertyModel<E> newTextEnum(ContainerModel type, NamespaceReference namespace, String name, 
			Class<E> enumClass, PropertyConstraint ... constraints) {
		return addConstraints(new EnumTextPropertyModel<E>(type, namespace.createQName(name), enumClass), constraints);
	}

	public static DatePropertyModel newDate(ContainerModel type, NamespaceReference namespace, String name) {
		return new DatePropertyModel(type, namespace.createQName(name));
	}
	public static MultiDatePropertyModel newMultiDate(ContainerModel type, NamespaceReference namespace, String name) {
		return new MultiDatePropertyModel(type, namespace.createQName(name));
	}
	public static DateTimePropertyModel newDateTime(ContainerModel type, NamespaceReference namespace, String name, PropertyConstraint ... constraints) {
		return addConstraints(new DateTimePropertyModel(type, namespace.createQName(name)), constraints);
	}
	public static MultiDateTimePropertyModel newMultiDateTime(ContainerModel type, NamespaceReference namespace, String name) {
		return new MultiDateTimePropertyModel(type, namespace.createQName(name));
	}
	public static LocalePropertyModel newLocale(ContainerModel type, NamespaceReference namespace, String name) {
		return new LocalePropertyModel(type, namespace.createQName(name));
	}

	public static IntegerPropertyModel newInteger(ContainerModel type, NamespaceReference namespace, String name) {
		return new IntegerPropertyModel(type, namespace.createQName(name));
	}
	public static MultiIntegerPropertyModel newMultiInteger(ContainerModel type, NamespaceReference namespace, String name) {
		return new MultiIntegerPropertyModel(type, namespace.createQName(name));
	}
	public static LongPropertyModel newLong(ContainerModel type, NamespaceReference namespace, String name) {
		return new LongPropertyModel(type, namespace.createQName(name));
	}
	public static LongPropertyModel newLong(ContainerModel type, NamespaceReference namespace, String name, PropertyConstraint ... constraints) {
		return addConstraints(new LongPropertyModel(type, namespace.createQName(name)), constraints);
	}
	public static MultiLongPropertyModel newMultiLong(ContainerModel type, NamespaceReference namespace, String name) {
		return new MultiLongPropertyModel(type, namespace.createQName(name));
	}
	public static FloatPropertyModel newFloat(ContainerModel type, NamespaceReference namespace, String name) {
		return new FloatPropertyModel(type, namespace.createQName(name));
	}
	public static MultiFloatPropertyModel newMultiFloat(ContainerModel type, NamespaceReference namespace, String name) {
		return new MultiFloatPropertyModel(type, namespace.createQName(name));
	}
	public static DoublePropertyModel newDouble(ContainerModel type, NamespaceReference namespace, String name) {
		return new DoublePropertyModel(type, namespace.createQName(name));
	}
	public static MultiDoublePropertyModel newMultiDouble(ContainerModel type, NamespaceReference namespace, String name) {
		return new MultiDoublePropertyModel(type, namespace.createQName(name));
	}

	public static BooleanPropertyModel newBoolean(ContainerModel type, NamespaceReference namespace, String name) {
		return new BooleanPropertyModel(type, namespace.createQName(name));
	}
	public static MultiBooleanPropertyModel newMultiBoolean(ContainerModel type, NamespaceReference namespace, String name) {
		return new MultiBooleanPropertyModel(type, namespace.createQName(name));
	}

	public static ContentPropertyModel newContent(ContainerModel type, NamespaceReference namespace, String name) {
		return new ContentPropertyModel(type, namespace.createQName(name));
	}
	public static NodeRefPropertyModel newNodeRef(ContainerModel type, NamespaceReference namespace, String name) {
		return new NodeRefPropertyModel(type, namespace.createQName(name));
	}
	public static MultiNodeRefPropertyModel newMultiNodeRef(ContainerModel type, NamespaceReference namespace, String name) {
		return new MultiNodeRefPropertyModel(type, namespace.createQName(name));
	}
	public static AssociationRefPropertyModel newAssociationRef(ContainerModel type, NamespaceReference namespace, String name) {
		return new AssociationRefPropertyModel(type, namespace.createQName(name));
	}
	public static MultiAssociationRefPropertyModel newMultiAssociationRef(ContainerModel type, NamespaceReference namespace, String name) {
		return new MultiAssociationRefPropertyModel(type, namespace.createQName(name));
	}
	public static ChildAssociationRefPropertyModel newChildAssociationRef(ContainerModel type, NamespaceReference namespace, String name) {
		return new ChildAssociationRefPropertyModel(type, namespace.createQName(name));
	}
	public static MultiChildAssociationRefPropertyModel newMultiChildAssociationRef(ContainerModel type, NamespaceReference namespace, String name) {
		return new MultiChildAssociationRefPropertyModel(type, namespace.createQName(name));
	}
	public static QNamePropertyModel newQName(ContainerModel type, NamespaceReference namespace, String name) {
		return new QNamePropertyModel(type, namespace.createQName(name));
	}
	public static MultiQNamePropertyModel newMultiQName(ContainerModel type, NamespaceReference namespace, String name) {
		return new MultiQNamePropertyModel(type, namespace.createQName(name));
	}

	public static AnyPropertyModel newAny(ContainerModel type, NamespaceReference namespace, String name) {
		return new AnyPropertyModel(type, namespace.createQName(name));
	}
	public static MultiAnyPropertyModel newMultiAny(ContainerModel type, NamespaceReference namespace, String name) {
		return new MultiAnyPropertyModel(type, namespace.createQName(name));
	}
}
