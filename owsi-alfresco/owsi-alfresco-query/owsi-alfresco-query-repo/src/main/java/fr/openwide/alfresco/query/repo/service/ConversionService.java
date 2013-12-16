package fr.openwide.alfresco.query.repo.service;

import java.io.Serializable;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.query.api.node.model.NameReference;
import fr.openwide.alfresco.query.api.node.model.NodeReference;

public interface ConversionService {

	NodeReference convert(NodeRef nodeRef);

	NodeRef convert(NodeReference nodeReference);

	QName convert(NameReference nameReference);

	NameReference convert(QName qname);

	Serializable convertToApp(Serializable value);
	Serializable convertToGed(Serializable value);

}
