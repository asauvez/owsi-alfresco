package fr.openwide.alfresco.query.web.form.projection.bean;

import org.springframework.beans.BeanWrapperImpl;

import fr.openwide.alfresco.query.web.form.projection.ProjectionBuilder;
import fr.openwide.alfresco.query.web.form.projection.ProjectionImpl;

public class BeanPropertyProjectionImpl<I, PB extends ProjectionBuilder<I, PB>, P> extends ProjectionImpl<I, PB, P> {

	private final String property;

	public BeanPropertyProjectionImpl(PB builder, String property, Class<P> propertyClass) {
		super(builder, propertyClass);
		this.property = property;
	}

	@Override
	public String getDefaultLabelCode() {
		return property;
	}

	@Override
	@SuppressWarnings("unchecked")
	public P apply(I value) {
		return (P) new BeanWrapperImpl(value).getPropertyValue(property);
	}

}
