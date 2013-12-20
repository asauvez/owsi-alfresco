package fr.openwide.alfresco.repository.remote.conversion.service.impl;

import java.io.Serializable;

import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.repository.api.node.model.RepositoryContentData;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;
import fr.openwide.alfresco.repository.remote.conversion.service.ConversionService;

public class ConversionServiceImpl implements ConversionService {

	private NamespacePrefixResolver namespacePrefixResolver;
	private MimetypeService mimetypeService;

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
		String prefix = namespacePrefixResolver.getPrefixes(qname.getNamespaceURI()).iterator().next();
		return NameReference.create(prefix, qname.getLocalName());
	}
	
	@Override
	public Serializable convertToApp(Serializable value) {
		if (value instanceof NodeRef) {
			return convert((NodeRef) value);
		} else if (value instanceof QName) {
			return convert((QName) value);
		} else if (value instanceof ContentData) {
			ContentData repoContent = (ContentData) value;

			String mimetypeDisplay = mimetypeService.getDisplaysByMimetype().get(repoContent.getMimetype());
			RepositoryContentData appContent = new RepositoryContentData(
					repoContent.getMimetype(), 
					(mimetypeDisplay != null) ? mimetypeDisplay : repoContent.getMimetype(), 
					repoContent.getSize(), 
					repoContent.getEncoding(), 
					repoContent.getLocale());
			
			return appContent;
		}
		return value;
	}

	@Override
	public Serializable convertToGed(Serializable value) {
		if (value instanceof NodeReference) {
			return convert((NodeReference) value);
		} else if (value instanceof NameReference) {
			return convert((NameReference) value);
		} else if (value instanceof RepositoryContentData) {
			// Sera traité de manière particulière directement.
			return value;
		}
		return value;
	}
	
	public void setNamespacePrefixResolver(NamespacePrefixResolver namespacePrefixResolver) {
		this.namespacePrefixResolver = namespacePrefixResolver;
	}
	public void setMimetypeService(MimetypeService mimetypeService) {
		this.mimetypeService = mimetypeService;
	}
}
