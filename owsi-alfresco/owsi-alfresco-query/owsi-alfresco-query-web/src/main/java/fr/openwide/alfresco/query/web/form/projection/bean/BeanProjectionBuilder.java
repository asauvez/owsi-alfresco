package fr.openwide.alfresco.query.web.form.projection.bean;

import fr.openwide.alfresco.query.web.form.projection.Projection;
import fr.openwide.alfresco.query.web.form.projection.ProjectionBuilder;

public class BeanProjectionBuilder<I> extends ProjectionBuilder<I, BeanProjectionBuilder<I>> {

	public <P> Projection<I, BeanProjectionBuilder<I>, P> prop(String property, Class<P> propertyClass) {
		return add(new BeanPropertyProjection<I, BeanProjectionBuilder<I>, P>(this, property, propertyClass));
	}

	public Projection<I, BeanProjectionBuilder<I>, String> propString(String property) {
		return prop(property, String.class);
	}

	public Projection<I, BeanProjectionBuilder<I>, Integer> propInteger(String property) {
		return prop(property, Integer.class);
	}

}
