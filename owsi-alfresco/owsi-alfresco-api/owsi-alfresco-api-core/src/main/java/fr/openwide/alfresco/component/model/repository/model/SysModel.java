package fr.openwide.alfresco.component.model.repository.model;

import fr.openwide.alfresco.api.core.remote.model.NamespaceReference;
import fr.openwide.alfresco.component.model.repository.model.sys.SysArchived;
import fr.openwide.alfresco.component.model.repository.model.sys.SysBase;
import fr.openwide.alfresco.component.model.repository.model.sys.SysCascadeUpdate;
import fr.openwide.alfresco.component.model.repository.model.sys.SysContainer;
import fr.openwide.alfresco.component.model.repository.model.sys.SysLocalized;
import fr.openwide.alfresco.component.model.repository.model.sys.SysReferenceable;
import fr.openwide.alfresco.component.model.repository.model.sys.SysTemporary;
import fr.openwide.alfresco.component.model.repository.model.sys.SysUndeletable;
import fr.openwide.alfresco.component.model.repository.model.sys.SysUnmovable;

public interface SysModel {

	// https://svn.alfresco.com/repos/alfresco-open-mirror/alfresco/HEAD/root/projects/repository/config/alfresco/model/systemModel.xml
	// https://github.com/Alfresco/alfresco-repository/blob/develop/src/main/resources/alfresco/model/systemModel.xml
	NamespaceReference NAMESPACE = NamespaceReference.create("sys", "http://www.alfresco.org/model/system/1.0");

	// ---- Aspects

	SysReferenceable referenceable = new SysReferenceable();
	
	SysLocalized localized = new SysLocalized();
	
	SysTemporary temporary = new SysTemporary();
	
	SysArchived archived = new SysArchived();
	
	SysCascadeUpdate cascadeUpdate = new SysCascadeUpdate();
	
	SysUndeletable undeletable = new SysUndeletable();
	SysUnmovable unmovable = new SysUnmovable();
	
	// ---- Types

	SysBase base = new SysBase();
	
	SysContainer container = new SysContainer();

}
