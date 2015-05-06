package fr.openwide.alfresco.component.query.form.projection;

import java.util.Comparator;

import org.springframework.context.MessageSourceResolvable;

import com.google.common.base.Function;

import fr.openwide.alfresco.app.web.pagination.SortParameters.SortDirection;
import fr.openwide.alfresco.component.query.form.view.output.OutputFieldView;


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
