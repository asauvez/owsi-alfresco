package fr.openwide.alfresco.component.query.search.service;

import java.util.List;

import fr.openwide.alfresco.component.query.form.result.FormQueryResult;
import fr.openwide.alfresco.component.query.search.model.BeanFormQuery;


public interface BeanFormQueryService {

	<I> FormQueryResult<I> list(BeanFormQuery<I> formQuery, List<I> list);
}
