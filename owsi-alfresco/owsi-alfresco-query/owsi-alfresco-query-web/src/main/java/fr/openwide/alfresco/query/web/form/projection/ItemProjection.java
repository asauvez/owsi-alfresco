package fr.openwide.alfresco.query.web.form.projection;

public class ItemProjection<I, PB extends ProjectionBuilder<I, PB>> extends ProjectionImpl<I, PB, I> {

	public ItemProjection(PB builder) {
		super(builder, Object.class);
	}

	@Override
	public I apply(I value) {
		return value;
	}

	@Override
	public String getDefaultLabelCode() {
		return "direct.label";
	}
}
