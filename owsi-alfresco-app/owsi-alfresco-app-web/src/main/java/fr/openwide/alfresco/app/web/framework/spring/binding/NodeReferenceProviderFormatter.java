package fr.openwide.alfresco.app.web.framework.spring.binding;

import java.text.ParseException;
import java.util.Locale;

import org.springframework.format.Formatter;

import fr.openwide.alfresco.app.core.node.model.NodeReferenceProvider;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;

public abstract class NodeReferenceProviderFormatter<T extends NodeReferenceProvider> implements Formatter<T> {

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
