package fr.openwide.alfresco.query.web.form.projection;

import fr.openwide.alfresco.query.web.form.view.output.OutputFieldView;

public class ItemProjectionImpl<I, PB extends ProjectionBuilder<I, PB>> extends ProjectionImpl<I, PB, I> {

	public ItemProjectionImpl(PB builder) {
		super(builder, Object.class);
		setView(OutputFieldView.CUSTOM);
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
