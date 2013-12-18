package fr.openwide.alfresco.query.core.repository.model;

import fr.openwide.alfresco.query.core.repository.model.cm.CmAuditable;
import fr.openwide.alfresco.query.core.repository.model.cm.CmAuthority;
import fr.openwide.alfresco.query.core.repository.model.cm.CmContent;
import fr.openwide.alfresco.query.core.repository.model.cm.CmFolder;
import fr.openwide.alfresco.query.core.repository.model.cm.CmObject;
import fr.openwide.alfresco.query.core.repository.model.cm.CmOwnable;
import fr.openwide.alfresco.query.core.repository.model.cm.CmPerson;
import fr.openwide.alfresco.query.core.repository.model.cm.CmTitled;

public interface CmModel {

	// ---- Types

	String NAMESPACE = "cm";

	CmObject object = new CmObject();

	CmContent content = new CmContent();

	CmFolder folder = new CmFolder();

	CmAuthority authority = new CmAuthority();

	CmPerson person = new CmPerson();

	// ---- Aspects

	CmTitled titled = new CmTitled();

	CmAuditable auditable = new CmAuditable();

	CmOwnable ownable = new CmOwnable();
}
