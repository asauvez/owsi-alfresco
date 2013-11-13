package fr.openwide.alfresco.query.web.form.projection;

import java.text.Collator;
import java.text.Format;
import java.util.Comparator;
import java.util.Date;

import org.springframework.context.MessageSourceResolvable;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Ordering;

import fr.openwide.alfresco.query.core.search.model.NodeFetchDetails;
import fr.openwide.alfresco.query.core.search.model.NodeResult;
import fr.openwide.alfresco.query.web.form.projection.button.ButtonBuilderImpl;
import fr.openwide.alfresco.query.web.form.projection.button.TopButtonBuilder;
import fr.openwide.alfresco.query.web.form.util.MessageUtils;
import fr.openwide.alfresco.query.web.form.view.output.IconOutputFieldView;
import fr.openwide.alfresco.query.web.form.view.output.OutputFieldView;
import fr.openwide.alfresco.query.web.search.model.PaginationParams.SortDirection;

public abstract class Projection<T> implements Function<NodeResult, T> {

	public enum Align { LEFT, CENTER, RIGHT };

	private final ProjectionBuilder builder;
	private MessageSourceResolvable label;
	
	private OutputFieldView outputFieldView = OutputFieldView.PLAIN;
	
	private Function<NodeResult, Object> nodeTransformer = null;
	private Function<? super T, Object> resultTransformer = Functions.identity();
	
	private SortDirection sortDirection = SortDirection.NONE;
	private int sortPriority = 0;
	private Comparator<? super T> comparator = null;
	
	private Align align = Align.LEFT;
	private boolean visible = true;

	@SuppressWarnings("unchecked")
	public Projection(ProjectionBuilder builder, Class<T> mappedClass) {
		this.builder = builder;
		if (String.class.equals(mappedClass)) {
			comparator(Ordering.from(Collator.getInstance()).nullsFirst());
		} else if (Comparable.class.isAssignableFrom(mappedClass)) {
			comparator((Comparator<T>) Ordering.natural().nullsFirst());
		}
		
		if (Number.class.isAssignableFrom(mappedClass)) {
			setOutputFieldView(OutputFieldView.NUMBER);
			align(Align.RIGHT);
		} else if (Date.class.isAssignableFrom(mappedClass)) {
			setOutputFieldView(OutputFieldView.DATE);
		} else if (Boolean.class.isAssignableFrom(mappedClass)) {
			transform(new Function<T, Object>() {
				@Override
				public Object apply(T value) {
					return ((Boolean) value) 
						? new IconOutputFieldView("glyphicon glyphicon-check", "boolean.true")
						: new IconOutputFieldView("", "boolean.false");
				}
			});
			setOutputFieldView(OutputFieldView.ICON);
			align(Align.CENTER);
		}
	}

	public ProjectionBuilder of() {
		return builder;
	}

	public Projection<T> label(String labelCode, Object ... labelArgs) {
		this.label = MessageUtils.code(labelCode, labelArgs);
		return this;
	}

	public Projection<T> visible(boolean visible) {
		this.visible = visible;
		return this;
	}

	public Projection<T> transform(Function<? super T, Object> resultTransformer) {
		this.resultTransformer = resultTransformer;
		setOutputFieldView(OutputFieldView.PLAIN);
		return this;
	}

	public Projection<T> format(final Format format) {
		return transform(new Function<T, Object>() {
			@Override
			public String apply(T input) {
				return format.format(input);
			}
		});
	}

	public TopButtonBuilder<Projection<T>, T> button(String message, Object ... messageArgs) {
		ButtonBuilderImpl<Projection<T>, T> buttonBuilder = new ButtonBuilderImpl<Projection<T>, T>(this, this, message, messageArgs);
		nodeTransformer = buttonBuilder;
		setOutputFieldView(OutputFieldView.BUTTON);
		return buttonBuilder;
	}

	public Projection<T> align(Align align) {
		this.align = align;
		return this;
	}

	public Projection<T> asEmail() {
		setOutputFieldView(OutputFieldView.EMAIL);
		return this;
	} 
	public Projection<T> asExternalLink() {
		setOutputFieldView(OutputFieldView.EXTERNAL_LINK);
		return this;
	} 
	public Projection<T> asCustom() {
		setOutputFieldView(OutputFieldView.CUSTOM);
		return this;
	} 
	
	public OutputFieldView getOutputFieldView() {
		return outputFieldView;
	}
	protected void setOutputFieldView(OutputFieldView outputFieldView) {
		this.outputFieldView = outputFieldView;
	}
	public Projection<T> comparator(Comparator<? super T> comparator) {
		this.comparator = comparator;
		return this;
	}
	
	public Projection<T> sort(SortDirection sortDirection, int sortPriority) {
		this.sortDirection = sortDirection;
		this.sortPriority = sortPriority;
		return this;
	}
	public Projection<T> sort(SortDirection sortDirection) {
		return sort(sortDirection, 0);
	}
	public Projection<T> sortAsc() {
		return sort(SortDirection.ASC, 0);
	}
	public Projection<T> sortDesc() {
		return sort(SortDirection.DESC, 0);
	}
	
	public SortDirection getSortDirection() {
		return sortDirection;
	}
	public int getSortPriority() {
		return sortPriority;
	}

	public Function<? super T, Object> getResultTransformer() {
		return resultTransformer;
	}
	public Function<NodeResult, Object> getNodeTransformer() {
		return (nodeTransformer != null) 
				? nodeTransformer 
				: Functions.compose(resultTransformer, this);
	}
		
	public Comparator<NodeResult> getNodeComparator() {
		return (comparator != null) 
			? new Comparator<NodeResult>() {
				@Override
				public int compare(NodeResult o1, NodeResult o2) {
					return comparator.compare(apply(o1), apply(o2));
				}
			} 
			: null;
	}
	
	public MessageSourceResolvable getLabel() {
		return label;
	}
	public abstract String getDefaultLabelCode();
	
	public boolean isVisible() {
		return visible;
	}

	public Align getAlign() {
		return align;
	}

	public void initNodeFetchDetails(@SuppressWarnings("unused") NodeFetchDetails nodeFetchDetails) {
		// to override
	}
}
