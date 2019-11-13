package fr.openwide.alfresco.repo.treeaspect.service;

import org.alfresco.service.namespace.QName;

public interface RegisterRootPropertyName {

	void registerCopyPropertyName(QName aspectOfRootNode, QName propertyToCopy);

	void registerCopyPropertyName(QName aspectOfRootNode, QName propertyToCopy, QName propertyWhereCopy);

}
