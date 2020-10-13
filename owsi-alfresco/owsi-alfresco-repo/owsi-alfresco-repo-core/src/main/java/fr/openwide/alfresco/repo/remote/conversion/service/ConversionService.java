package fr.openwide.alfresco.repo.remote.conversion.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.api.core.remote.model.StoreReference;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.EnumTextPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.SinglePropertyModel;

public interface ConversionService {

	NodeReference get(NodeRef nodeRef);

	NodeRef getRequired(NodeReference nodeReference);

	StoreRef getRequired(StoreReference storeReference);

	NameReference get(QName qname);

	QName getRequired(NameReference nameReference);

	Serializable getForApplication(Serializable value);
	Serializable getForRepository(Serializable value);

	Map<NameReference, Serializable> getForApplication(Map<QName, Serializable> properties);
	Map<QName, Serializable> getForRepository(Map<NameReference, Serializable> properties);

	<C extends Serializable> C getProperty(Map<QName, Serializable> value, SinglePropertyModel<C> property);
	<C extends Serializable> List<C> getProperty(Map<QName, Serializable> value, MultiPropertyModel<C> property);
	<E extends Enum<E>> E getProperty(Map<QName, Serializable> value, EnumTextPropertyModel<E> property);
}
