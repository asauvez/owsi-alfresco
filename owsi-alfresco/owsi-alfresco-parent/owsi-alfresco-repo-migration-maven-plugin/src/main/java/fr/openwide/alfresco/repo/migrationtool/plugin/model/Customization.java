package fr.openwide.alfresco.repo.migrationtool.plugin.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="customization")
public class Customization {

	public String targetPackageRoot;
	public String sourcePackageRoot;
	
}
