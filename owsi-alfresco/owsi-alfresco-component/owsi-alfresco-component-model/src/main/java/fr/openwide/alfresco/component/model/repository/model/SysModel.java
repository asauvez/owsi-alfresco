package fr.openwide.alfresco.component.model.repository.model;

import fr.openwide.alfresco.component.model.repository.model.sys.SysBase;
import fr.openwide.alfresco.component.model.repository.model.sys.SysLocalized;
import fr.openwide.alfresco.component.model.repository.model.sys.SysReferenceable;
import fr.openwide.alfresco.component.model.repository.model.sys.SysTemporary;

public interface SysModel {

	// https://svn.alfresco.com/repos/alfresco-open-mirror/alfresco/HEAD/root/projects/repository/config/alfresco/model/systemModel.xml
	String NAMESPACE = "sys";

	// ---- Aspects

	SysReferenceable referenceable = new SysReferenceable();
	
	SysLocalized localized = new SysLocalized();
	
	SysTemporary temporary = new SysTemporary();
	
	// ---- Types

	SysBase base = new SysBase();

}
