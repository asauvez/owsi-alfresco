package fr.openwide.alfresco.query.web.search.service;

import java.util.List;

import fr.openwide.alfresco.query.web.form.result.FormQueryResult;
import fr.openwide.alfresco.query.web.search.model.BeanFormQuery;


public interface BeanFormQueryService {

	<I> FormQueryResult<I> list(BeanFormQuery<I> formQuery, List<I> list);
}
