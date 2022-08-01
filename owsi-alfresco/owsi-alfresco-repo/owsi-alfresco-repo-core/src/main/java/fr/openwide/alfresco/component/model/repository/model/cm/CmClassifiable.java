package fr.openwide.alfresco.component.model.repository.model.cm;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmClassifiable extends AspectModel {

	public CmClassifiable() {
		super(CmModel.NAMESPACE.createQName("classifiable"));
	}

	protected CmClassifiable(QName qName) {
		super(qName);
	}

}
