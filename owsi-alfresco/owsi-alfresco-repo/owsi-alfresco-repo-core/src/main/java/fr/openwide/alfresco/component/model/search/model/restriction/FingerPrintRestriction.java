package fr.openwide.alfresco.component.model.search.model.restriction;

import org.alfresco.service.cmr.repository.NodeRef;

public class FingerPrintRestriction extends Restriction {

	private final NodeRef nodeRef;
	private Integer overlap;
	private Integer confident;

	public FingerPrintRestriction(RestrictionBuilder parent, NodeRef nodeRef) {
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
		return "FINGERPRINT:" + nodeRef.getId()
				+ ((overlap != null) ? "_" + overlap : "")
				+ ((confident != null) ? "_" + confident : "");
	}
}
