package fr.openwide.alfresco.component.query.form.projection;

import java.text.Collator;
import java.text.Format;
import java.util.Comparator;
import java.util.Date;

import org.springframework.context.MessageSourceResolvable;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Ordering;

import fr.openwide.alfresco.app.web.pagination.SortParameters.SortDirection;
import fr.openwide.alfresco.component.query.form.projection.button.ButtonBuilderImpl;
import fr.openwide.alfresco.component.query.form.projection.button.TopButtonBuilder;
import fr.openwide.alfresco.component.query.form.util.MessageUtils;
import fr.openwide.alfresco.component.query.form.view.output.IconOutputFieldView;
import fr.openwide.alfresco.component.query.form.view.output.OutputFieldView;

public abstract class ProjectionImpl<I, PB extends ProjectionBuilder<I, PB>, P>
	implements 
		Projection<I, PB, P>,
		ProjectionColumn<I>,
		Function<I, P>,
		ProjectionVisitorAcceptor {

	private final PB builder;
	private String id;
	private MessageSourceResolvable label;

	private OutputFieldView view = OutputFieldView.PLAIN;

	private Function<I, Object> itemTransformer = null;
	private Function<? super P, Object> resultTransformer = Functions.identity();

	private SortDirection sortDirection = SortDirection.NONE;
	private int sortPriority = 0;
	private Comparator<? super P> comparator = null;

	private Align align = Align.LEFT;
	private boolean visible = true;

	public ProjectionImpl(PB builder, Class<? super P> mappedClass) {
		this.builder = builder;
		if (String.class.equals(mappedClass)) {
			comparator(Ordering.from(Collator.getInstance()).nullsFirst());
			comparatorToString();
		} else if (Comparable.class.isAssignableFrom(mappedClass)) {
			comparatorNatural();
		}

		if (Number.class.isAssignableFrom(mappedClass)) {
			setView(OutputFieldView.NUMBER);
			align(Align.RIGHT);
		} else if (Date.class.isAssignableFrom(mappedClass)) {
			setView(OutputFieldView.DATE);
		} else if (Boolean.class.isAssignableFrom(mappedClass)) {
			transform(new Function<P, Object>() {
				@Override
				public Object apply(P value) {
					return (value != null && (Boolean) value)
						? new IconOutputFieldView("glyphicon glyphicon-check", "boolean.true")
						: new IconOutputFieldView("", "boolean.false");
				}
			});
			setView(OutputFieldView.ICON);
			align(Align.CENTER);
		}
	}

	@Override
	public PB of() {
		return builder;
	}

	@Override
	public Projection<I, PB, P> id(String id) {
		this.id = id;
		return this;
	}
	
	@Override
	public ProjectionImpl<I, PB, P> label(String labelCode, Object ... labelArgs) {
		this.label = MessageUtils.code(labelCode, labelArgs);
		return this;
	}
	@Override
	public Projection<I, PB, P> labelEmpty() {
		this.label = MessageUtils.direct("");
		return this;
	}

	@Override
	public ProjectionImpl<I, PB, P> visible(boolean visible) {
		this.visible = visible;
		return this;
	}

	@Override
	public ProjectionImpl<I, PB, P> transform(Function<? super P, Object> resultTransformer) {
		this.resultTransformer = resultTransformer;
		setView(OutputFieldView.PLAIN);
		return this;
	}

	@Override
	public ProjectionImpl<I, PB, P> format(final Format format) {
		return transform(new Function<P, Object>() {
			@Override
			public String apply(P input) {
				return format.format(input);
			}
		});
	}

	@Override
	public TopButtonBuilder<Projection<I, PB, P>, I> button(String message, Object ... messageArgs) {
		ButtonBuilderImpl<Projection<I, PB, P>, I> buttonBuilder
			= new ButtonBuilderImpl<Projection<I, PB, P>, I>(this, getItemTransformer(), message, messageArgs);
		itemTransformer = buttonBuilder;
		setView(OutputFieldView.BUTTON);
		comparatorNone();
		return buttonBuilder;
	}

	@Override
	public ProjectionImpl<I, PB, P> align(Align align) {
		this.align = align;
		return this;
	}

	@Override
	public Projection<I, PB, P> asCheckBox() {
		return setView(OutputFieldView.CHECKBOX)
			.align(Align.CENTER);
	}
	@Override
	public ProjectionImpl<I, PB, P> asEmail() {
		return setView(OutputFieldView.EMAIL);
	}
	@Override
	public ProjectionImpl<I, PB, P> asExternalLink() {
		return setView(OutputFieldView.EXTERNAL_LINK);
	}
	@Override
	public ProjectionImpl<I, PB, P> asCustom() {
		return setView(OutputFieldView.CUSTOM);
	}

	@Override
	public OutputFieldView getView() {
		return view;
	}
	public ProjectionImpl<I, PB, P> setView(OutputFieldView outputFieldView) {
		this.view = outputFieldView;
		return this;
	}
	@Override
	public ProjectionImpl<I, PB, P> comparator(Comparator<? super P> comparator) {
		this.comparator = comparator;
		return this;
	}

	@Override
	public ProjectionImpl<I, PB, P> comparatorNone() {
		return comparator((Comparator<P>) null);
	}
	@Override
	public Projection<I, PB, P> comparator(Function<? super P, ? extends Comparable<?>> transformer) {
		return comparator(Ordering.natural().nullsLast().onResultOf(transformer));
	}
	@Override
	@SuppressWarnings("unchecked")
	public ProjectionImpl<I, PB, P> comparatorNatural() {
		return comparator((Comparator<? super P>) Ordering.natural().nullsFirst());
	}
	@Override
	public ProjectionImpl<I, PB, P> comparatorToString() {
		final Collator collator = Collator.getInstance();
		return comparator(Ordering.from(new Comparator<P>() {
			@Override
			public int compare(P o1, P o2) {
				return collator.compare(o1.toString(), o2.toString());
			}
		}).nullsFirst());
	}

	@Override
	public ProjectionImpl<I, PB, P> sort(SortDirection sortDirection, int sortPriority) {
		this.sortDirection = sortDirection;
		this.sortPriority = sortPriority;
		return this;
	}
	@Override
	public ProjectionImpl<I, PB, P> sort(SortDirection sortDirection) {
		return sort(sortDirection, 0);
	}
	@Override
	public ProjectionImpl<I, PB, P> sortAsc() {
		return sort(SortDirection.ASC, 0);
	}
	@Override
	public ProjectionImpl<I, PB, P> sortDesc() {
		return sort(SortDirection.DESC, 0);
	}

	@Override
	public SortDirection getSortDirection() {
		return sortDirection;
	}
	@Override
	public int getSortPriority() {
		return sortPriority;
	}

	public Function<? super P, Object> getResultTransformer() {
		return resultTransformer;
	}
	@Override
	public Function<I, Object> getItemTransformer() {
		return (itemTransformer != null)
				? itemTransformer
				: Functions.compose(resultTransformer, this);
	}

	@Override
	public Comparator<I> getItemComparator() {
		return (comparator != null)
			? new Comparator<I>() {
				@Override
				public int compare(I o1, I o2) {
					return comparator.compare(apply(o1), apply(o2));
				}
			}
			: null;
	}

	@Override
	public String getId() {
		return id;
	}
	@Override
	public MessageSourceResolvable getLabel() {
		return label;
	}
	public void setLabel(MessageSourceResolvable label) {
		this.label = label;
	}
	public abstract String getDefaultLabelCode();

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public String getAlign() {
		return "text-" + align.name().toLowerCase();
	}

	@Override
	public void accept(ProjectionVisitor visitor) {
		visitor.visit(itemTransformer);
		visitor.visit(comparator);
	}

}
