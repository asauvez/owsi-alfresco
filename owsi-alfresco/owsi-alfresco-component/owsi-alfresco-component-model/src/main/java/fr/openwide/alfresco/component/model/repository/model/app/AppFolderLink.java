package fr.openwide.alfresco.component.model.repository.model.app;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.repository.model.AppModel;
import fr.openwide.alfresco.component.model.repository.model.cm.CmLink;

public class AppFolderLink extends CmLink {
	
	public AppFolderLink() {
		super(NameReference.create(AppModel.NAMESPACE, "folderlink"));
	}

	protected AppFolderLink(NameReference nameReference) {
		super(nameReference);
	}

}
