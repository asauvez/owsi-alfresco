package fr.openwide.alfresco.component.model.repository.model.app;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.repository.model.AppModel;
import fr.openwide.alfresco.component.model.repository.model.cm.CmLink;

public class AppFileLink extends CmLink {
	
	public AppFileLink() {
		super(NameReference.create(AppModel.NAMESPACE, "filelink"));
	}

	protected AppFileLink(NameReference nameReference) {
		super(nameReference);
	}

}
