package fr.openwide.alfresco.repository.core.remote.service;

import java.io.Serializable;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;
import fr.openwide.alfresco.repository.api.remote.model.StoreReference;

public interface ConversionService {

	NodeReference get(NodeRef nodeRef);

	NodeRef getRequired(NodeReference nodeReference);

	StoreRef getRequired(StoreReference storeReference);

	NameReference get(QName qname);

	QName getRequired(NameReference nameReference);

	Serializable getForApplication(Serializable value);
	Serializable getForRepository(Serializable value);

}
