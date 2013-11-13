package fr.openwide.alfresco.query.web.search.service;

import java.util.List;

import fr.openwide.alfresco.query.web.form.result.FormQueryResult;
import fr.openwide.alfresco.query.web.search.model.AbstractFormQuery;


public interface FormQueryService {
	
	<T> FormQueryResult<T> list(AbstractFormQuery<T> formQuery, List<T> list);
}
