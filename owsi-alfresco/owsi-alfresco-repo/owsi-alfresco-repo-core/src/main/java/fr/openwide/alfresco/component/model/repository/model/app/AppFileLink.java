package fr.openwide.alfresco.component.model.repository.model.app;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.repository.model.AppModel;
import fr.openwide.alfresco.component.model.repository.model.cm.CmLink;

public class AppFileLink extends CmLink {
	
	public AppFileLink() {
		super(AppModel.NAMESPACE.createQName("filelink"));
	}

	protected AppFileLink(QName qName) {
		super(qName);
	}

}
