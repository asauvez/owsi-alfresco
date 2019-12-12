package fr.openwide.alfresco.test.model;

import java.util.ArrayList;
import java.util.List;

public class PathElementModelIT {
	
	private String id;
	private String name;
	private String nodeType;
	private List<String> aspectNames = new ArrayList<String>();
	
	public PathElementModelIT() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	public List<String> getAspectNames() {
		return aspectNames;
	}

	public void setAspectNames(List<String> aspectNames) {
		this.aspectNames = aspectNames;
	}
	
}
