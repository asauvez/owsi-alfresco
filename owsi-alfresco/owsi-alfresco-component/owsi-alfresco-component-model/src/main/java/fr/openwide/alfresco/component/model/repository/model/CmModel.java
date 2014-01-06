package fr.openwide.alfresco.component.model.repository.model;

import fr.openwide.alfresco.component.model.repository.model.cm.CmAuditable;
import fr.openwide.alfresco.component.model.repository.model.cm.CmAuthor;
import fr.openwide.alfresco.component.model.repository.model.cm.CmAuthority;
import fr.openwide.alfresco.component.model.repository.model.cm.CmAuthorityContainer;
import fr.openwide.alfresco.component.model.repository.model.cm.CmContent;
import fr.openwide.alfresco.component.model.repository.model.cm.CmFolder;
import fr.openwide.alfresco.component.model.repository.model.cm.CmObject;
import fr.openwide.alfresco.component.model.repository.model.cm.CmOwnable;
import fr.openwide.alfresco.component.model.repository.model.cm.CmPerson;
import fr.openwide.alfresco.component.model.repository.model.cm.CmTitled;
import fr.openwide.alfresco.component.model.repository.model.cm.CmVersionable;

public interface CmModel {

	// ---- Types

	String NAMESPACE = "cm";

	CmObject object = new CmObject();

	CmContent content = new CmContent();

	CmFolder folder = new CmFolder();

	CmAuthority authority = new CmAuthority();

	CmAuthorityContainer authorityContainer = new CmAuthorityContainer();
	
	CmPerson person = new CmPerson();

	// ---- Aspects

	CmTitled titled = new CmTitled();

	CmAuditable auditable = new CmAuditable();

	CmVersionable versionable = new CmVersionable();

	CmOwnable ownable = new CmOwnable();

	CmAuthor author = new CmAuthor();
}
