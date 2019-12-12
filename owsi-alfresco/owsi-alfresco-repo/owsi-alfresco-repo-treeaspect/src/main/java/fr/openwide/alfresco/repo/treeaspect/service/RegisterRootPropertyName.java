package fr.openwide.alfresco.repo.treeaspect.service;

import org.alfresco.service.namespace.QName;

public interface RegisterRootPropertyName {

	void registerCopyPropertyCmName(QName aspectOfRootNode, QName propertyToCopy);

	void registerCopyProperty(QName aspectOfRootNode, QName propertyToCopy, QName propertyWhereCopy);

}
