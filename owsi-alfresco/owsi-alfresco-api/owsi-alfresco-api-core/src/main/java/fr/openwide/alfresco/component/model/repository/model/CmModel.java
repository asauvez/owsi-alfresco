package fr.openwide.alfresco.component.model.repository.model;

import fr.openwide.alfresco.api.core.remote.model.NamespaceReference;
import fr.openwide.alfresco.component.model.repository.model.cm.CmAttachable;
import fr.openwide.alfresco.component.model.repository.model.cm.CmAuditable;
import fr.openwide.alfresco.component.model.repository.model.cm.CmAuthor;
import fr.openwide.alfresco.component.model.repository.model.cm.CmAuthority;
import fr.openwide.alfresco.component.model.repository.model.cm.CmAuthorityContainer;
import fr.openwide.alfresco.component.model.repository.model.cm.CmCategory;
import fr.openwide.alfresco.component.model.repository.model.cm.CmCategoryRoot;
import fr.openwide.alfresco.component.model.repository.model.cm.CmCheckedOut;
import fr.openwide.alfresco.component.model.repository.model.cm.CmClassifiable;
import fr.openwide.alfresco.component.model.repository.model.cm.CmContent;
import fr.openwide.alfresco.component.model.repository.model.cm.CmCopiedFrom;
import fr.openwide.alfresco.component.model.repository.model.cm.CmCountable;
import fr.openwide.alfresco.component.model.repository.model.cm.CmEmailed;
import fr.openwide.alfresco.component.model.repository.model.cm.CmFolder;
import fr.openwide.alfresco.component.model.repository.model.cm.CmGeneralClassifiable;
import fr.openwide.alfresco.component.model.repository.model.cm.CmGeographic;
import fr.openwide.alfresco.component.model.repository.model.cm.CmIndexControl;
import fr.openwide.alfresco.component.model.repository.model.cm.CmLink;
import fr.openwide.alfresco.component.model.repository.model.cm.CmLockable;
import fr.openwide.alfresco.component.model.repository.model.cm.CmObject;
import fr.openwide.alfresco.component.model.repository.model.cm.CmOwnable;
import fr.openwide.alfresco.component.model.repository.model.cm.CmPerson;
import fr.openwide.alfresco.component.model.repository.model.cm.CmPreferences;
import fr.openwide.alfresco.component.model.repository.model.cm.CmStoreSelector;
import fr.openwide.alfresco.component.model.repository.model.cm.CmSummarizable;
import fr.openwide.alfresco.component.model.repository.model.cm.CmTitled;
import fr.openwide.alfresco.component.model.repository.model.cm.CmVersionable;
import fr.openwide.alfresco.component.model.repository.model.cm.CmWorkingCopy;

public interface CmModel {

	// https://github.com/Alfresco/alfresco-repository/blob/develop/src/main/resources/alfresco/model/contentModel.xml
	NamespaceReference NAMESPACE = NamespaceReference.create("cm", "http://www.alfresco.org/model/content/1.0");

	// ---- Aspects

	CmTitled titled = new CmTitled();

	CmAuditable auditable = new CmAuditable();

	CmVersionable versionable = new CmVersionable();
	
	CmSummarizable summarizable = new CmSummarizable();
	
	CmCountable countable = new CmCountable();
	
	CmLockable lockable = new CmLockable();

	CmCheckedOut checkedOut = new CmCheckedOut();
	
	CmOwnable ownable = new CmOwnable();

	CmAuthor author = new CmAuthor();
	
	CmCopiedFrom copiedFrom = new CmCopiedFrom();
	
	CmWorkingCopy workingCopy = new CmWorkingCopy();
	
	CmStoreSelector storeSelector = new CmStoreSelector();
	
	CmClassifiable classifiable = new CmClassifiable();
	
	CmGeneralClassifiable generalClassifiable = new CmGeneralClassifiable();

	CmGeographic geographic = new CmGeographic();
	
	CmIndexControl indexControl = new CmIndexControl();
	
	CmPreferences preferences = new CmPreferences();
	
	CmAttachable attachable = new CmAttachable();
	
	CmEmailed emailed = new CmEmailed();
	
	// ---- Types

	CmObject object = new CmObject();

	CmContent content = new CmContent();

	CmFolder folder = new CmFolder();

	CmAuthority authority = new CmAuthority();

	CmAuthorityContainer authorityContainer = new CmAuthorityContainer();
	
	CmPerson person = new CmPerson();

	CmCategoryRoot categoryRoot = new CmCategoryRoot();
	CmCategory category = new CmCategory();
	
	CmLink link = new CmLink();
}
