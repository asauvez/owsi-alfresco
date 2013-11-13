package fr.openwide.alfresco.query.web.search.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import fr.openwide.alfresco.query.web.form.result.FormQueryResult;
import fr.openwide.alfresco.query.web.search.model.AbstractFormQuery;
import fr.openwide.alfresco.query.web.search.service.FormQueryService;

@Service
public class FormQueryServiceImpl extends AbstractFormQueryService implements FormQueryService {
	
	@Override
	public <T> FormQueryResult<T> list(AbstractFormQuery<T> formQuery, List<T> list) {
		FormQueryResult<T> result = new FormQueryResult<T>();
		return initResult(formQuery, result, list);
	}
	
}
