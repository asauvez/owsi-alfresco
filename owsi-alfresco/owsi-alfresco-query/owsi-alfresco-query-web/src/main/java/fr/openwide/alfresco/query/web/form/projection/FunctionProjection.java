package fr.openwide.alfresco.query.web.form.projection;

import com.google.common.base.Function;

public class FunctionProjection<I, PB extends ProjectionBuilder<I, PB>, P> extends ProjectionImpl<I, PB, P> {

	private Function<I, P> transformer;

	public FunctionProjection(PB builder, Function<I, P> transformer) {
		super(builder, Object.class);
		this.transformer = transformer;
	}

	@Override
	public P apply(I value) {
		return transformer.apply(value);
	}

	@Override
	public String getDefaultLabelCode() {
		return "transformer.label";
	}
}
