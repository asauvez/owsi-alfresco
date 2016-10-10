package fr.openwide.alfresco.component.model.node.model;

import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class AssociationModel extends Model {

	public AssociationModel(NameReference nameReference) {
		super(nameReference);
	}
	
	public String getXmlModel() {
		return getXmlModel(0);
	}

	public String getXmlModel(int profondeur) {

		StringBuilder xml = new StringBuilder();
		StringBuilder tabulation = new StringBuilder();
		
		for (int i = 0; i < profondeur; i++){
			tabulation.append("	");
		}
		
		xml.append(tabulation.toString()).append("<association name=\"") .append(this.getNameReference().getFullName()).append("\">\n")
			.append(tabulation.toString()).append("	TODO \n")
			.append(tabulation.toString()).append("</association>\n");
		
		return xml.toString();
	}

}
