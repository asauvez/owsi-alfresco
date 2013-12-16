package fr.openwide.alfresco.query.repo.service.impl;

import java.io.Serializable;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.query.api.node.model.NameReference;
import fr.openwide.alfresco.query.api.node.model.NodeReference;
import fr.openwide.alfresco.query.repo.service.ConversionService;

public class ConversionServiceImpl implements ConversionService {

	private NamespacePrefixResolver namespacePrefixResolver;

	@Override
	public NodeReference convert(NodeRef nodeRef) {
		return NodeReference.create(nodeRef.toString());
	}
	@Override
	public NodeRef convert(NodeReference nodeReference) {
		return new NodeRef(nodeReference.getReference());
	}
	@Override
	public QName convert(NameReference nameReference) {
		return QName.createQName(nameReference.toString(), namespacePrefixResolver);
	}
	@Override
	public NameReference convert(QName qname) {
		return NameReference.create(qname.getPrefixString(), qname.getLocalName());
	}
	
	@Override
	public Serializable convertToApp(Serializable value) {
		if (value instanceof NodeRef) {
			return convert((NodeRef) value);
		} else if (value instanceof QName) {
			return convert((QName) value);
		}
		return value;
	}

	@Override
	public Serializable convertToGed(Serializable value) {
		if (value instanceof NodeReference) {
			return convert((NodeReference) value);
		} else if (value instanceof NameReference) {
			return convert((NameReference) value);
		}
		return value;
	}
	
	public void setNamespacePrefixResolver(NamespacePrefixResolver namespacePrefixResolver) {
		this.namespacePrefixResolver = namespacePrefixResolver;
	}
}
