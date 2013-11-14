package fr.openwide.alfresco.query.web.form.projection;

import java.text.Collator;
import java.text.Format;
import java.util.Comparator;
import java.util.Date;

import org.springframework.context.MessageSourceResolvable;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Ordering;

import fr.openwide.alfresco.query.web.form.projection.button.ButtonBuilderImpl;
import fr.openwide.alfresco.query.web.form.projection.button.TopButtonBuilder;
import fr.openwide.alfresco.query.web.form.util.MessageUtils;
import fr.openwide.alfresco.query.web.form.view.output.IconOutputFieldView;
import fr.openwide.alfresco.query.web.form.view.output.OutputFieldView;
import fr.openwide.alfresco.query.web.search.model.PaginationParams.SortDirection;

public abstract class ProjectionImpl<I, PB extends ProjectionBuilder<I, PB>, P> 
	implements Projection<I, PB, P>, Function<I, P>, ProjectionColumn<I> {

	private final PB builder;
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
					return ((Boolean) value) 
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
	public ProjectionImpl<I, PB, P> label(String labelCode, Object ... labelArgs) {
		this.label = MessageUtils.code(labelCode, labelArgs);
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
	public ProjectionImpl<I, PB, P> asEmail() {
		setView(OutputFieldView.EMAIL);
		return this;
	} 
	@Override
	public ProjectionImpl<I, PB, P> asExternalLink() {
		setView(OutputFieldView.EXTERNAL_LINK);
		return this;
	} 
	@Override
	public ProjectionImpl<I, PB, P> asCustom() {
		setView(OutputFieldView.CUSTOM);
		return this;
	} 
	
	@Override
	public OutputFieldView getView() {
		return view;
	}
	protected void setView(OutputFieldView outputFieldView) {
		this.view = outputFieldView;
	}
	@Override
	public ProjectionImpl<I, PB, P> comparator(Comparator<? super P> comparator) {
		this.comparator = comparator;
		return this;
	}

	@Override
	public ProjectionImpl<I, PB, P> comparatorNone() {
		return comparator(null);
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

}
