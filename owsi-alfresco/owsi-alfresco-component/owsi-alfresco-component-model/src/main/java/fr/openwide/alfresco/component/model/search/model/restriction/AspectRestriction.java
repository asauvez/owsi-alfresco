package fr.openwide.alfresco.component.model.search.model.restriction;

import java.util.Set;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.ContainerModel;

public class AspectRestriction extends Restriction {

	private final AspectModel aspect;
	private boolean exact = false;

	public AspectRestriction(RestrictionBuilder parent, AspectModel aspect) {
		super(parent);
		this.aspect = aspect;
	}

	public AspectRestriction exact() {
		exact = true;
		return this;
	}

	@Override
	protected String toFtsQueryInternal() {
		return ((exact) ? "EXACTASPECT:" : "ASPECT:") + aspect.toLucene();
	}
	
	@Override
	protected void addCmisQueryJoin(Set<ContainerModel> containersToJoin) {
		containersToJoin.add(aspect);
	}
	
	@Override
	protected String toCmisQueryWhereInternal() {
		if (isNot()) {
			throw new UnsupportedOperationException("not aspect " + aspect);
		}
		return "";
	}
}
