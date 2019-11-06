package fr.openwide.alfresco.test.model;

import java.util.ArrayList;
import java.util.List;

public class PathInfoModelIT {
	
	private String name;
	private Boolean isComplete;
	private List<PathElementModelIT> elements = new ArrayList<PathElementModelIT>();
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getIsComplete() {
		return isComplete;
	}

	public void setIsComplete(Boolean isComplete) {
		this.isComplete = isComplete;
	}

	public List<PathElementModelIT> getElements() {
		return elements;
	}

	public void setElements(List<PathElementModelIT> elements) {
		this.elements = elements;
	}
	
	
	
	
	

}
