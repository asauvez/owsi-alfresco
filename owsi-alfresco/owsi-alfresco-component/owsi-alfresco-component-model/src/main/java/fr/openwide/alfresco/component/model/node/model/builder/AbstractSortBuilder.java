package fr.openwide.alfresco.component.model.node.model.builder;

import fr.openwide.alfresco.api.core.search.model.RepositorySortDefinition;
import fr.openwide.alfresco.component.model.node.model.property.single.SinglePropertyModel;


public class AbstractSortBuilder<B extends AbstractQueryBuilder<B, ?, ?>> {

	private B builder;
	
	public void init(B builder) {
		this.builder = builder;
	}
	
	public B asc(SinglePropertyModel<? extends Comparable<?>> property) {
		return sort(property, true);
	}
	public B desc(SinglePropertyModel<? extends Comparable<?>> property) {
		return sort(property, false);
	}
	public B sort(SinglePropertyModel<? extends Comparable<?>> property, boolean ascending) {
		builder.getParameters().getSorts().add(new RepositorySortDefinition(property.getNameReference(), ascending));
		return builder;
	}

}
