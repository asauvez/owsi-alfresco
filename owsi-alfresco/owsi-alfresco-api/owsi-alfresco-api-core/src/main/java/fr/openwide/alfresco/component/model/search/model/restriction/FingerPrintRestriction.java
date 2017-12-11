package fr.openwide.alfresco.component.model.search.model.restriction;

import fr.openwide.alfresco.api.core.remote.model.NodeReference;

public class FingerPrintRestriction extends Restriction {

	private final NodeReference nodeRef;
	private Integer overlap;
	private Integer confident;

	public FingerPrintRestriction(RestrictionBuilder parent, NodeReference nodeRef) {
		super(parent);
		this.nodeRef = nodeRef;
	}
	
	public FingerPrintRestriction overlap(Integer percent) {
		this.overlap = percent;
		return this;
	}
	public FingerPrintRestriction confident(Integer percent) {
		this.confident = percent;
		return this;
	}

	@Override
	protected String toFtsQueryInternal() {
		return "FINGERPRINT:" + nodeRef.getUuid()
				+ ((overlap != null) ? "_" + overlap : "")
				+ ((confident != null) ? "_" + confident : "");
	}
}
