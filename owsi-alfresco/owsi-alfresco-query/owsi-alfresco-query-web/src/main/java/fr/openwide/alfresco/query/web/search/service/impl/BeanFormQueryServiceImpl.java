package fr.openwide.alfresco.query.web.search.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import fr.openwide.alfresco.query.web.form.projection.bean.BeanProjectionBuilder;
import fr.openwide.alfresco.query.web.form.result.FormQueryResult;
import fr.openwide.alfresco.query.web.search.model.BeanFormQuery;
import fr.openwide.alfresco.query.web.search.service.BeanFormQueryService;

@Service
public class BeanFormQueryServiceImpl extends AbstractFormQueryService implements BeanFormQueryService {

	@Override
	public <I> FormQueryResult<I> list(BeanFormQuery<I> formQuery, List<I> list) {
		BeanProjectionBuilder<I> projectionBuilder = new BeanProjectionBuilder<I>();
		formQuery.initBeanProjections(projectionBuilder);
		FormQueryResult<I> result = createQueryResult(formQuery, projectionBuilder);
		return initResult(formQuery, result, list);
	}

}
