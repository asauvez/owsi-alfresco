package fr.openwide.alfresco.component.model.repository.model.app;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.repository.model.AppModel;
import fr.openwide.alfresco.component.model.repository.model.cm.CmLink;

public class AppFolderLink extends CmLink {
	
	public AppFolderLink() {
		super(AppModel.NAMESPACE.createQName("folderlink"));
	}

	protected AppFolderLink(QName qName) {
		super(qName);
	}

}
