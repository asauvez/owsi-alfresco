package fr.openwide.alfresco.app.web.framework.spring.binding;

import java.text.ParseException;
import java.util.Locale;

import org.springframework.format.Formatter;

import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

public class NodeReferenceFormatter implements Formatter<NodeReference> {

	@Override
	public NodeReference parse(String text, Locale locale) throws ParseException {
		return NodeReference.create(text);
	}

	@Override
	public String print(NodeReference object, Locale locale) {
		return object.getReference();
	}

}
