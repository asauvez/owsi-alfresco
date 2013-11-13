package fr.openwide.alfresco.query.core.search.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import fr.openwide.alfresco.query.core.node.model.value.NameReference;

public class NodeFetchDetails implements Serializable {

	private static final long serialVersionUID = 6930653481257487738L;

	private boolean type;
	private Set<NameReference> properties = new HashSet<>();
	private Set<NameReference> aspects = new HashSet<>();;
	
	// private NodeFetchDetails primaryParent;
	// private Map<NameReference, NodeFetchDetails> associations;
	// private Map<NameReference, NodeFetchDetails> childAssociations;
	
	public boolean isType() {
		return type;
	}
	public void setTypeFect(boolean type) {
		this.type = type;
	}
	public Set<NameReference> getProperties() {
		return properties;
	}
	public Set<NameReference> getAspects() {
		return aspects;
	}

}
