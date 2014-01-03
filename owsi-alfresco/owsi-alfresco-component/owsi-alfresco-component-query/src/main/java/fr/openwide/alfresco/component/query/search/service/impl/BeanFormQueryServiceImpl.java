package fr.openwide.alfresco.component.query.search.service.impl;

import java.util.List;

import fr.openwide.alfresco.component.query.form.projection.bean.BeanProjectionBuilder;
import fr.openwide.alfresco.component.query.form.result.FormQueryResult;
import fr.openwide.alfresco.component.query.search.model.BeanFormQuery;
import fr.openwide.alfresco.component.query.search.service.BeanFormQueryService;

public class BeanFormQueryServiceImpl extends AbstractFormQueryService implements BeanFormQueryService {

	@Override
	public <I> FormQueryResult<I> list(BeanFormQuery<I> formQuery, List<I> list) {
		BeanProjectionBuilder<I> projectionBuilder = new BeanProjectionBuilder<I>();
		formQuery.initBeanProjections(projectionBuilder);
		FormQueryResult<I> result = createQueryResult(formQuery, projectionBuilder);
		return initResult(formQuery, result, list);
	}

}
