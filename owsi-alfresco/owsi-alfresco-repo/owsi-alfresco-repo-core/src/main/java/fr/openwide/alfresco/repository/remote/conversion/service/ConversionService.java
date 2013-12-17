package fr.openwide.alfresco.repository.remote.conversion.service;

import java.io.Serializable;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

public interface ConversionService {

	NodeReference convert(NodeRef nodeRef);

	NodeRef convert(NodeReference nodeReference);

	QName convert(NameReference nameReference);

	NameReference convert(QName qname);

	Serializable convertToApp(Serializable value);
	Serializable convertToGed(Serializable value);

}
