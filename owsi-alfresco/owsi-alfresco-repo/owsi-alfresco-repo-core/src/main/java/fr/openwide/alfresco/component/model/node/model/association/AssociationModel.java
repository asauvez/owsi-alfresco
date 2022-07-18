package fr.openwide.alfresco.component.model.node.model.association;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.Model;

public abstract class AssociationModel extends Model {

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
			.append(tabulation.toString()).append("	<source>\n")
			.append(tabulation.toString()).append("		<role>TODO<role>\n")
			.append(tabulation.toString()).append("		<mandatory>" + isFromMany() + "<mandatory>\n")
			.append(tabulation.toString()).append("		<many>true/false<many>\n")
			.append(tabulation.toString()).append("	</source>\n")
			.append(tabulation.toString()).append("	<target>\n")
			.append(tabulation.toString()).append("		<role>TODO<role>\n")
			.append(tabulation.toString()).append("		<mandatory>" + isToMany() + "<mandatory>\n")
			.append(tabulation.toString()).append("		<many>true/false<many>\n")
			.append(tabulation.toString()).append("	</target>\n")
			.append(tabulation.toString()).append("</association>\n");

		return xml.toString();
	}
	
	public abstract boolean isToMany();
	public abstract boolean isFromMany();

}
