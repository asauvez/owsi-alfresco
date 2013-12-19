package fr.openwide.alfresco.component.model.search.restriction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.TypeModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.TextPropertyModel;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

public class RestrictionBuilder extends Restriction {

	public enum LogicalOperator { AND, OR }

	private final List<Restriction> restrictions = new ArrayList<>();
	private final LogicalOperator operator;

	public RestrictionBuilder() {
		this (null, LogicalOperator.AND);
	}

	public RestrictionBuilder(RestrictionBuilder parent, LogicalOperator operator) {
		super(parent);
		this.operator = operator;
	}

	public RestrictionBuilder or() {
		return add(new RestrictionBuilder(this, LogicalOperator.OR));
	}

	public RestrictionBuilder and() {
		return add(new RestrictionBuilder(this, LogicalOperator.AND));
	}

	public TypeRestriction isType(TypeModel type) {
		return add(new TypeRestriction(this, type));
	}

	public AspectRestriction hasAspect(AspectModel aspect) {
		return add(new AspectRestriction(this, aspect));
	}

	public ParentRestriction parent(NodeReference parentRef) {
		return add(new ParentRestriction(this, parentRef));
	}

	public TextMatchRestriction match(TextPropertyModel property, String value) {
		return add(new TextMatchRestriction(this, property, value));
	}

	public <C extends Serializable> MatchRestriction<C> match(PropertyModel<C> property, C value) {
		return add(new MatchRestriction<C>(this, property, value));
	}

	public MatchAllRestriction matchAll(String value) {
		return add(new MatchAllRestriction(this, value));
	}

	public <C extends Serializable> BetweenRestriction<C> between(PropertyModel<C> property, C from, C to) {
		return add(new BetweenRestriction<C>(this, property, from, to));
	}

	public <C extends Serializable> BetweenRestriction<C> ge(PropertyModel<C> property, C value) {
		return between(property, value, null);
	}
	public <C extends Serializable> BetweenRestriction<C> gt(PropertyModel<C> property, C value) {
		return between(property, value, null)
				.minInclusive(false);
	}
	public <C extends Serializable> BetweenRestriction<C> lt(PropertyModel<C> property, C value) {
		return between(property, null, value)
				.maxInclusive(false);
	}
	public <C extends Serializable> BetweenRestriction<C> le(PropertyModel<C> property, C value) {
		return between(property, null, value);
	}

	public CustomRestriction custom(String customQuery) {
		return add(new CustomRestriction(this, customQuery));
	}

	private <R extends Restriction> R add(R restriction) {
		restrictions.add(restriction);
		return restriction;
	}

	@Override
	protected String toLuceneQueryInternal() {
		StringBuilder buf = new StringBuilder();
		for (Restriction restriction : restrictions) {
			String query = restriction.toLuceneQuery();
			if (query.length() > 0) {
				if (buf.length() > 0) {
					buf.append("\n").append(operator.name()).append(" ");
				}
				buf.append("(")
					.append(query)
					.append(")");
			}
		}
		return buf.toString();
	}

}
