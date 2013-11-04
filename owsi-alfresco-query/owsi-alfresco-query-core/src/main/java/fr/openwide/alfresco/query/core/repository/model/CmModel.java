package fr.openwide.alfresco.query.core.repository.model;

import fr.openwide.alfresco.query.core.repository.model.cm.CmAuthority;
import fr.openwide.alfresco.query.core.repository.model.cm.CmContent;
import fr.openwide.alfresco.query.core.repository.model.cm.CmFolder;
import fr.openwide.alfresco.query.core.repository.model.cm.CmObject;
import fr.openwide.alfresco.query.core.repository.model.cm.CmPerson;

public interface CmModel {

	String NAMESPACE = "cm";

	CmObject object = new CmObject();

	CmContent content = new CmContent();

	CmFolder folder = new CmFolder();

	CmAuthority authority = new CmAuthority();

	CmPerson person = new CmPerson();

}
