package fr.openwide.alfresco.query.core.search.projection;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Function;

import fr.openwide.alfresco.query.core.node.model.property.PropertyModel;

public class ProjectionBuilder {

	private final List<Projection<?>> projections = new ArrayList<Projection<?>>();
	private Function<Object, String> resultFormatter;

	public ProjectionBuilder(Function<Object, String> resultFormatter) {
		this.resultFormatter = resultFormatter;
	}

	public RefProjection ref() {
		return add(new RefProjection(this));
	}

	public TypeProjection type() {
		return add(new TypeProjection(this));
	}

	public <T> PropertyProjection<T> prop(PropertyModel<T> property) {
		return add(new PropertyProjection<T>(this, property));
	}

	protected <P extends Projection<?>> P add(P projection) {
		projections.add(projection);
		return projection;
	}

	public List<Projection<?>> getProjections() {
		return projections;
	}

	public Function<Object, String> getResultFormatter() {
		return resultFormatter;
	}

}
