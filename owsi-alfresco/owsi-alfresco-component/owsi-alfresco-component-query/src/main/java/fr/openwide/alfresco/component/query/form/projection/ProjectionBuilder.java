package fr.openwide.alfresco.component.query.form.projection;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Function;

public class ProjectionBuilder<I, PB extends ProjectionBuilder<I, PB>>
	implements ProjectionVisitorAcceptor {

	private final List<ProjectionImpl<I, ? extends ProjectionBuilder<I, ?>, ?>> projections = new ArrayList<>();

	public Projection<I, PB, I> item() {
		return add(new ItemProjectionImpl<I, PB>(getThis()));
	}

	public <P> Projection<I, PB, P> function(Function<I, P> function) {
		return add(new FunctionProjectionImpl<I, PB, P>(getThis(), function));
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
	
	@Override
	public void accept(ProjectionVisitor visitor) {
		for (ProjectionImpl<I, ?, ?> projection : getProjections()) {
			visitor.visit(projection);
		}
	}
}
