package fr.openwide.alfresco.query.web.form.projection;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Function;

public class ProjectionBuilder<I, PB extends ProjectionBuilder<I, PB>> {

	private final List<ProjectionImpl<I, ? extends ProjectionBuilder<I, ?>, ?>> projections = new ArrayList<>();
	
	public Projection<I, PB, I> item() {
		return add(new ItemProjection<I, PB>(getThis()));
	}
	
	public <P> Projection<I, PB, P> function(Function<I, P> function) {
		return add(new FunctionProjection<I, PB, P>(getThis(), function));
	}
	
	protected <P extends ProjectionImpl<I, PB, ?>> P add(P projection) {
		projections.add(projection);
		return projection;
	}

	public List<ProjectionImpl<I, ? extends ProjectionBuilder<I, ?>, ?>> getProjections() {
		return projections;
	}

	@SuppressWarnings("unchecked")
	private PB getThis() {
		return (PB) this;
	}
}
