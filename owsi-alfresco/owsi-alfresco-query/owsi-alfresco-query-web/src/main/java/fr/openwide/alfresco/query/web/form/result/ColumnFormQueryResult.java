package fr.openwide.alfresco.query.web.form.result;

import java.util.Comparator;

import org.springframework.context.MessageSourceResolvable;

import com.google.common.base.Function;
import com.google.common.base.Functions;

import fr.openwide.alfresco.query.web.form.view.output.OutputFieldView;
import fr.openwide.alfresco.query.web.search.model.PaginationParams.SortDirection;

public class ColumnFormQueryResult<T> {

	private final MessageSourceResolvable label;
	private String align = null;
	private OutputFieldView outputFieldView;

	private Function<T, ?> transformer = Functions.identity();
	private Comparator<T> comparator = null;
	private SortDirection sortDirection = SortDirection.NONE;
	private int sortPriority = 0;

	public ColumnFormQueryResult(MessageSourceResolvable label, OutputFieldView outputFieldView) {
		this.label = label;
		this.outputFieldView = outputFieldView;
	}

	public MessageSourceResolvable getLabel() {
		return label;
	}

	public String getView() {
		return outputFieldView.name();
	}
	
	public Function<T, ?> getTransformer() {
		return transformer;
	}
	public Comparator<T> getComparator() {
		return comparator;
	}
	public int getSortPriority() {
		return sortPriority;
	}
	
	public ColumnFormQueryResult<T> transformer(Function<T, ?> transformer) {
		this.transformer = transformer;
		return this;
	}
	public ColumnFormQueryResult<T> comparator(Comparator<T> comparator) {
		this.comparator = comparator;
		return this;
	}
	public ColumnFormQueryResult<T> sort(SortDirection sortDirection, int sortPriority) {
		this.sortDirection = sortDirection;
		this.sortPriority = sortPriority;
		return this;
	}
	
	public String getAlign() {
		return align;
	}
	public ColumnFormQueryResult<T> align(String align) {
		this.align = align;
		return this;
	}

	public SortDirection getSortDirection() {
		return sortDirection;
	}
}
