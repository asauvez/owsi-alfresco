package fr.openwide.alfresco.component.model.node.model.property;

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
import fr.openwide.alfresco.component.model.node.model.property.single.FloatPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.IntegerPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.LocalePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.LongPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.NameReferencePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.NodeReferencePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public final class PropertyModels {

	public static <M extends PropertyModel<?>> M addConstraints(M propertyModel, PropertyConstraint ... constraints) {
		for (PropertyConstraint constraint : constraints) {
			propertyModel.add(constraint);
		}
		return propertyModel;
	}
	
	public static TextPropertyModel newText(ContainerModel type, String name, PropertyConstraint ... constraints) {
		return addConstraints(new TextPropertyModel(type, NameReference.create(type.getNameReference().getNamespace(), name)), constraints);
	}
	public static MultiTextPropertyModel newMultiText(ContainerModel type, String name) {
		return new MultiTextPropertyModel(type, NameReference.create(type.getNameReference().getNamespace(), name));
	}

	public static DatePropertyModel newDate(ContainerModel type, String name) {
		return new DatePropertyModel(type, NameReference.create(type.getNameReference().getNamespace(), name));
	}
	public static MultiDatePropertyModel newMultiDate(ContainerModel type, String name) {
		return new MultiDatePropertyModel(type, NameReference.create(type.getNameReference().getNamespace(), name));
	}
	public static DateTimePropertyModel newDateTime(ContainerModel type, String name, PropertyConstraint ... constraints) {
		return addConstraints(new DateTimePropertyModel(type, NameReference.create(type.getNameReference().getNamespace(), name)), constraints);
	}
	public static MultiDateTimePropertyModel newMultiDateTime(ContainerModel type, String name) {
		return new MultiDateTimePropertyModel(type, NameReference.create(type.getNameReference().getNamespace(), name));
	}
	public static LocalePropertyModel newLocale(ContainerModel type, String name) {
		return new LocalePropertyModel(type, NameReference.create(type.getNameReference().getNamespace(), name));
	}

	public static IntegerPropertyModel newInteger(ContainerModel type, String name) {
		return new IntegerPropertyModel(type, NameReference.create(type.getNameReference().getNamespace(), name));
	}
	public static MultiIntegerPropertyModel newMultiInteger(ContainerModel type, String name) {
		return new MultiIntegerPropertyModel(type, NameReference.create(type.getNameReference().getNamespace(), name));
	}
	public static LongPropertyModel newLong(ContainerModel type, String name) {
		return new LongPropertyModel(type, NameReference.create(type.getNameReference().getNamespace(), name));
	}
	public static MultiLongPropertyModel newMultiLong(ContainerModel type, String name) {
		return new MultiLongPropertyModel(type, NameReference.create(type.getNameReference().getNamespace(), name));
	}
	public static FloatPropertyModel newFloat(ContainerModel type, String name) {
		return new FloatPropertyModel(type, NameReference.create(type.getNameReference().getNamespace(), name));
	}
	public static MultiFloatPropertyModel newMultiFloat(ContainerModel type, String name) {
		return new MultiFloatPropertyModel(type, NameReference.create(type.getNameReference().getNamespace(), name));
	}
	public static DoublePropertyModel newDouble(ContainerModel type, String name) {
		return new DoublePropertyModel(type, NameReference.create(type.getNameReference().getNamespace(), name));
	}
	public static MultiDoublePropertyModel newMultiDouble(ContainerModel type, String name) {
		return new MultiDoublePropertyModel(type, NameReference.create(type.getNameReference().getNamespace(), name));
	}

	public static BooleanPropertyModel newBoolean(ContainerModel type, String name) {
		return new BooleanPropertyModel(type, NameReference.create(type.getNameReference().getNamespace(), name));
	}
	public static MultiBooleanPropertyModel newMultiBoolean(ContainerModel type, String name) {
		return new MultiBooleanPropertyModel(type, NameReference.create(type.getNameReference().getNamespace(), name));
	}

	public static ContentPropertyModel newContent(ContainerModel type, String name) {
		return new ContentPropertyModel(type, NameReference.create(type.getNameReference().getNamespace(), name));
	}

	public static NodeReferencePropertyModel newNodeReference(ContainerModel type, String name) {
		return new NodeReferencePropertyModel(type, NameReference.create(type.getNameReference().getNamespace(), name));
	}
	public static MultiNodeReferencePropertyModel newMultiNodeReference(ContainerModel type, String name) {
		return new MultiNodeReferencePropertyModel(type, NameReference.create(type.getNameReference().getNamespace(), name));
	}
	public static NameReferencePropertyModel newNameReference(ContainerModel type, String name) {
		return new NameReferencePropertyModel(type, NameReference.create(type.getNameReference().getNamespace(), name));
	}
	public static MultiNameReferencePropertyModel newMultiNameReference(ContainerModel type, String name) {
		return new MultiNameReferencePropertyModel(type, NameReference.create(type.getNameReference().getNamespace(), name));
	}

}
