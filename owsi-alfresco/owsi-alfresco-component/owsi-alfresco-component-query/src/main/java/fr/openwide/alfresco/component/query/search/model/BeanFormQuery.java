package fr.openwide.alfresco.component.query.search.model;

import fr.openwide.alfresco.component.query.form.projection.bean.BeanProjectionBuilder;

public abstract class BeanFormQuery<I> extends AbstractFormQuery<I> {

	public void initBeanProjections(BeanProjectionBuilder<I> builder) {
		// to override
		builder
			.item();
	}

}
