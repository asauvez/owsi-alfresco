package fr.openwide.alfresco.app.web.framework.spring.binding;

import java.text.ParseException;
import java.util.Locale;

import org.springframework.format.Formatter;

import fr.openwide.alfresco.app.core.node.model.NodeReferenceAware;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

public abstract class NodeReferenceAwareFormatter<T extends NodeReferenceAware> implements Formatter<T> {

	private NodeReferenceFormatter nodeReferenceFormatter = new NodeReferenceFormatter();

	@Override
	public T parse(String text, Locale locale) throws ParseException {
		NodeReference nodeReference = nodeReferenceFormatter.parse(text, locale);
		return parse(nodeReference);
	}

	protected abstract T parse(NodeReference source) throws ParseException;

	@Override
	public String print(T object, Locale locale) {
		return nodeReferenceFormatter.print(object.getNodeReference(), locale);
	}

}
