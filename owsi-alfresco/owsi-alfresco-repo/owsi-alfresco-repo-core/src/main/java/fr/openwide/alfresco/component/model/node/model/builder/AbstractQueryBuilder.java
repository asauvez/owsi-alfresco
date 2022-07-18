package fr.openwide.alfresco.component.model.node.model.builder;

import fr.openwide.alfresco.api.core.node.model.AbstractQueryParameters;

public abstract class AbstractQueryBuilder<B extends AbstractQueryBuilder<B, P, S>, P extends AbstractQueryParameters, S extends AbstractSortBuilder<B>> {

	private final P parameters;
	private final S sortBuilder;

	protected AbstractQueryBuilder(P parameters, S sortBuilder) {
		this.parameters = parameters;
		this.sortBuilder = sortBuilder;
		this.sortBuilder.init(getThis());
	}

	@SuppressWarnings("unchecked")
	private B getThis() {
		return (B) this;
	}

	public S sort() {
		return sortBuilder;
	}

	public P getParameters() {
		return parameters;
	}
}
