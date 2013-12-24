package fr.openwide.alfresco.app.web.framework.spring.converter;

import org.springframework.core.convert.converter.Converter;

import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class NameReferenceConverter implements Converter<String, NameReference>{

	@Override
	public NameReference convert(String source) {
		return NameReference.create(source);
	}
}
