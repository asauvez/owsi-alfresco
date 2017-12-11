package fr.openwide.alfresco.component.model.search.model.restriction;

import java.io.Serializable;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.component.model.node.model.TypeModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.AbstractDatePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.ContentPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;

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
	
	/**
	 * Permet de faire un OR entre plusieurs conditions :
	 * <pre>
	 * 		.or()
	 * 			.condition1().of()
	 * 			.condition2().of()
	 * 			.of()
	 * </pre>
	 */
	public RestrictionBuilder or() {
		return add(new RestrictionBuilder(this, LogicalOperator.OR));
	}

	/**
	 * Permet de faire un AND entre plusieurs conditions :
	 * <pre>
	 * 		.and()
	 * 			.condition1().of()
	 * 			.condition2().of()
	 * 			.of()
	 * </pre>
	 */
	public RestrictionBuilder and() {
		return add(new RestrictionBuilder(this, LogicalOperator.AND));
	}

	public TypeRestriction isType(TypeModel type) {
		return add(new TypeRestriction(this, type));
	}

	public PathRestriction path(String path) {
		return add(new PathRestriction(this, path));
	}

	public AspectRestriction hasAspect(AspectModel aspect) {
		return add(new AspectRestriction(this, aspect));
	}

	public IdRestriction id(NodeReference nodeRef) {
		return add(new IdRestriction(this, nodeRef));
	}

	/**
	 * Est-ce que les noeuds retournés sont les fils directes du noeuds en question
	 */
	public ParentRestriction parent(NodeReference parentRef) {
		return add(new ParentRestriction(this, parentRef));
	}

	/**
	 * Si la paramètre est null ou chaîne vide, on ne tient pas compte de la restriction.
	 */
	public MatchRestriction<String> match(PropertyModel<String> property, String value) {
		return add(new MatchRestriction<String>(this, property, value));
	}
	public MatchRestriction<String> match(ContentPropertyModel property, String value) {
		TextPropertyModel textPropertyModel = new TextPropertyModel(property.getType(), property.getNameReference());
		return add(new MatchRestriction<String>(this, textPropertyModel, value));
	}

	public <C extends Serializable> MatchRestriction<C> eq(PropertyModel<C> property, C value) {
		return add(new MatchRestriction<C>(this, property, value).exact(true));
	}
	public MatchRestriction<String> startsWith(TextPropertyModel property, String value) {
		if (value != null) {
			value += "*";
		}
		return add(new MatchRestriction<String>(this, property, value).exact(true));
	}

	public MatchAllRestriction matchAll(String value) {
		return add(new MatchAllRestriction(this, value));
	}
	/**
	 * Recherche uniquement dans les propriétés de type d:content
	 */
	public MatchTextRestriction matchText(String value) {
		return add(new MatchTextRestriction(this, value));
	}

	public <C extends Serializable>  RestrictionBuilder in(PropertyModel<C> property, @SuppressWarnings("unchecked") C ... values) {
		return in(property, Arrays.asList(values));
	}
	
	public <C extends Serializable>  RestrictionBuilder in(PropertyModel<C> property, Collection<C> values) {
		RestrictionBuilder or = or();
		for (C value : values) {
			or.eq(property, value).of();
		}
		return or;
	}

	public <C extends Serializable> BetweenRestriction<C> between(PropertyModel<C> property, C from, C to) {
		return add(new BetweenValueRestriction<C>(this, property, from, to));
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

	public BetweenRestriction<Date> lt(AbstractDatePropertyModel property, Integer max, TemporalUnit unit) {
		return between(property, null, max, unit);
	}
	public BetweenRestriction<Date> gt(AbstractDatePropertyModel property, Integer min, TemporalUnit unit) {
		return between(property, min, null, unit);
	}
	public BetweenRestriction<Date> between(AbstractDatePropertyModel property, Integer min, Integer max, TemporalUnit unit) {
		return add(new BetweenRelativeTimeRestriction(this, property, min, max, unit));
	}
	
	public TagRestriction tag(String tag) {
		return add(new TagRestriction(this, tag));
	}
	public FingerPrintRestriction fingerPrint(NodeReference nodeReference) {
		return add(new FingerPrintRestriction(this, nodeReference));
	}

	public CustomRestriction custom(String customFtsQuery) {
		return add(new CustomRestriction(this, customFtsQuery));
	}

	public <R extends Restriction> R add(R restriction) {
		if (restriction != null) {
			restrictions.add(restriction);
		}
		return restriction;
	}

	@Override
	public void testInit(NodeScopeBuilder nodeScopeBuilder) {
		for (Restriction restriction : restrictions) {
			restriction.testInit(nodeScopeBuilder);
		}
	}
	@Override
	public boolean test(BusinessNode node) {
		switch (operator) {
		case AND:
			for (Restriction restriction : restrictions) {
				if (! restriction.test(node)) {
					return false;
				}
			}
			return true;
		case OR:
			for (Restriction restriction : restrictions) {
				if (restriction.test(node)) {
					return true;
				}
			}
			return false;
		}
		throw new IllegalStateException();
	}
	
	@Override
	protected boolean isNeedingParenthesis() {
		return true;
	}
	
	@Override
	protected String toFtsQueryInternal() {
		StringBuilder buf = new StringBuilder();
		for (Restriction restriction : restrictions) {
			String query = restriction.toFtsQuery();
			if (query.length() > 0) {
				if (buf.length() > 0) {
					buf.append("\n").append(operator.name()).append(" ");
				}
				boolean needingParenthesis = restriction.isNeedingParenthesis() && ! query.startsWith("NOT ");
				if (needingParenthesis) buf.append("(");
				buf.append(query.replace("\n", "\n\t"));
				if (needingParenthesis) buf.append(")");
			}
		}
		return buf.toString();
	}
	
	@Override
	protected void addCmisQueryJoin(Set<ContainerModel> containersToJoin) {
		for (Restriction restriction : restrictions) {
			restriction.addCmisQueryJoin(containersToJoin);
		}
	}
	
	@Override
	protected String toCmisQueryWhereInternal() {
		StringBuilder buf = new StringBuilder();
		for (Restriction restriction : restrictions) {
			String query = restriction.toCmisWhereQuery();
			if (query.length() > 0) {
				if (buf.length() > 0) {
					buf.append("\n").append(operator.name()).append(" ");
				}
				boolean needingParenthesis = restriction.isNeedingParenthesis() && ! query.startsWith("NOT ");
				if (needingParenthesis) buf.append("(");
				buf.append(query.replace("\n", "\n\t"));
				if (needingParenthesis) buf.append(")");
			}
		}
		return buf.toString();
	}

}
