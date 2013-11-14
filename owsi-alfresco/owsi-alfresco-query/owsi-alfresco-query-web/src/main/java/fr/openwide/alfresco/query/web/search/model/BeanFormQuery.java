package fr.openwide.alfresco.query.web.search.model;

import fr.openwide.alfresco.query.web.form.projection.bean.BeanProjectionBuilder;

public abstract class BeanFormQuery<I> extends AbstractFormQuery<I> {

	public void initBeanProjections(BeanProjectionBuilder<I> builder) {
		// to override
		builder
			.item();
	}

}
