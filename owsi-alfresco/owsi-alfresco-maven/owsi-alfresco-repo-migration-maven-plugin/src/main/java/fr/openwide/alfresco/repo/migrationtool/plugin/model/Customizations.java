package fr.openwide.alfresco.repo.migrationtool.plugin.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="customizations")
public class Customizations {

	@XmlElement(name = "customization")
	public List<Customization> customizations = new ArrayList<Customization>();
}
