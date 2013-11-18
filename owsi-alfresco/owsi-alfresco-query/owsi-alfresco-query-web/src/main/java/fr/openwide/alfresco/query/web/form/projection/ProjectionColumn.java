package fr.openwide.alfresco.query.web.form.projection;

import java.util.Comparator;

import org.springframework.context.MessageSourceResolvable;

import com.google.common.base.Function;

import fr.openwide.alfresco.query.web.form.view.output.OutputFieldView;
import fr.openwide.alfresco.query.web.search.model.PaginationParams.SortDirection;


public interface ProjectionColumn<I> {

	String getId();
	MessageSourceResolvable getLabel();
	OutputFieldView getView();

	boolean isVisible();
	String getAlign();
	Function<I, Object> getItemTransformer();

	SortDirection getSortDirection();
	int getSortPriority();

	ProjectionColumn<I> sort(SortDirection sortDirection, int sortPriority);
	Comparator<I> getItemComparator();

}
