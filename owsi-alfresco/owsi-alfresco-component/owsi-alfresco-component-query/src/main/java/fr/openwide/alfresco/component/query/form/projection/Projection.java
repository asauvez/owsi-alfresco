package fr.openwide.alfresco.component.query.form.projection;

import java.text.Format;
import java.util.Comparator;

import com.google.common.base.Function;

import fr.openwide.alfresco.app.web.pagination.SortParams.SortDirection;
import fr.openwide.alfresco.component.query.form.projection.button.TopButtonBuilder;

public interface Projection<I, PB extends ProjectionBuilder<I, PB>, P> {

	public enum Align { LEFT, CENTER, RIGHT	};

	PB of();

	Projection<I, PB, P> id(String id);
	Projection<I, PB, P> label(String labelCode, Object ... labelArgs);
	Projection<I, PB, P> labelEmpty();
	Projection<I, PB, P> visible(boolean visible);

	Projection<I, PB, P> transform(Function<? super P, Object> resultTransformer);
	Projection<I, PB, P> format(final Format format);
	Projection<I, PB, P> align(Align align);

	TopButtonBuilder<Projection<I, PB, P>, I> button(String message, Object ... messageArgs);
	Projection<I, PB, P> asCheckBox();
	Projection<I, PB, P> asEmail();
	Projection<I, PB, P> asExternalLink();
	Projection<I, PB, P> asCustom();

	Projection<I, PB, P> comparator(Comparator<? super P> comparator);
	Projection<I, PB, P> comparator(Function<? super P, ? extends Comparable<?>> transformer);
	Projection<I, PB, P> comparatorNone();
	Projection<I, PB, P> comparatorNatural();
	Projection<I, PB, P> comparatorToString();

	Projection<I, PB, P> sort(SortDirection sortDirection, int sortPriority);
	Projection<I, PB, P> sort(SortDirection sortDirection);
	Projection<I, PB, P> sortAsc();
	Projection<I, PB, P> sortDesc();

}
