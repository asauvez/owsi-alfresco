package fr.openwide.alfresco.query.web.search.model;

import com.google.common.base.Function;

import fr.openwide.alfresco.query.core.repository.model.CmModel;
import fr.openwide.alfresco.query.core.search.model.NodeResult;
import fr.openwide.alfresco.query.web.form.projection.ProjectionBuilder;

public abstract class NodeFormQuery extends AbstractFormQuery<NodeResult> {

	public void initProjections(ProjectionBuilder builder) {
		// to override
		builder
			.ref().of()
			.type().of()
			.prop(CmModel.object.name).of();
	}
	
	public String formatObject(Object o, Function<Object, String> defaultFormatter) {
		// to override
		return defaultFormatter.apply(o);
	}
	
}
