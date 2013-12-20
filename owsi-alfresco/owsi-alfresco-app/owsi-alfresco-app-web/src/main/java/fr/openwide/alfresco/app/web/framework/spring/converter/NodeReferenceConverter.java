package fr.openwide.alfresco.app.web.framework.spring.converter;

import org.springframework.core.convert.converter.Converter;

import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

public class NodeReferenceConverter implements Converter<String, NodeReference>{

	@Override
	public NodeReference convert(String source) {
		return NodeReference.create(source);
	}
}
