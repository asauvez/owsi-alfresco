package fr.openwide.alfresco.app.web.framework.spring.binding;

import java.text.ParseException;
import java.util.Locale;

import org.springframework.format.Formatter;

import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

public abstract class NodeReferenceBasedFormatter<T> implements Formatter<T> {

	private NodeReferenceFormatter nodeReferenceFormatter = new NodeReferenceFormatter();

	@Override
	public T parse(String text, Locale locale) throws ParseException {
		NodeReference nodeReference = nodeReferenceFormatter.parse(text, locale);
		return parse(nodeReference);
	}

	protected abstract T parse(NodeReference source);

	@Override
	public String print(T object, Locale locale) {
		NodeReference nodeReference = format(object);
		return nodeReferenceFormatter.print(nodeReference, locale);
	}

	protected abstract NodeReference format(T source);

}
