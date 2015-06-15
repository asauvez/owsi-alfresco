package fr.openwide.alfresco.repository.remote.conversion.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.api.core.node.model.RepositoryContentData;
import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.api.core.remote.model.StoreReference;
import fr.openwide.alfresco.repository.remote.conversion.service.ConversionService;
import fr.openwide.alfresco.repository.remote.framework.exception.InvalidPayloadException;

public class ConversionServiceImpl implements ConversionService {

	private NamespacePrefixResolver namespacePrefixResolver;
	private MimetypeService mimetypeService;

	@Override
	public NodeReference get(NodeRef nodeRef) {
		return NodeReference.create(nodeRef.toString());
	}

	@Override
	public NodeRef getRequired(NodeReference nodeReference) {
		if (nodeReference == null) {
			throw new InvalidPayloadException("Node reference is required");
		}
		return get(nodeReference);
	}

	private NodeRef get(NodeReference nodeReference) {
		return new NodeRef(nodeReference.getReference());
	}

	@Override
	public StoreRef getRequired(StoreReference storeReference) {
		if (storeReference == null) {
			throw new InvalidPayloadException("Store reference is required");
		}
		return new StoreRef(storeReference.getReference());
	}

	@Override
	public NameReference get(QName qname) {
		String prefix = namespacePrefixResolver.getPrefixes(qname.getNamespaceURI()).iterator().next();
		return NameReference.create(prefix, qname.getLocalName());
	}

	@Override
	public QName getRequired(NameReference nameReference) {
		if (nameReference == null) {
			throw new InvalidPayloadException("Name reference is required");
		}
		return get(nameReference);
	}

	private QName get(NameReference nameReference) {
		return QName.createQName(nameReference.toString(), namespacePrefixResolver);
	}

	@Override
	public Serializable getForApplication(Serializable value) {
		if (value instanceof NodeRef) {
			return get((NodeRef) value);
		} else if (value instanceof QName) {
			return get((QName) value);
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
		} else if (value instanceof List) {
			ArrayList<Serializable> res = new ArrayList<Serializable>();
			for (Object o : (List<?>) value) {
				res.add(getForApplication((Serializable) o));
			}
			return res;
		}
		return value;
	}

	@Override
	public Serializable getForRepository(Serializable value) {
		if (value instanceof NodeReference) {
			return get((NodeReference) value);
		} else if (value instanceof NameReference) {
			return get((NameReference) value);
		} else if (value instanceof RepositoryContentData) {
			// Sera traité ultérieurement
			return value;
		} else if (value instanceof List) {
			ArrayList<Serializable> res = new ArrayList<Serializable>();
			for (Object o : (List<?>) value) {
				res.add(getForRepository((Serializable) o));
			}
			return res;
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
