package org.owsi.alfresco.repo.datalistgroupe.model;

import org.alfresco.service.namespace.QName;

public interface DatalistAuthorityModel {
	
	String DATALISTAUTHORITY_NAMESPACE = "http://openwide.fr/modele/dlauthority";
	
	QName TYPE_DATALISTAUTHORITY_ITEM = QName.createQName(DATALISTAUTHORITY_NAMESPACE, "item");
	
	QName ASPECT_DATALISTAUTHORITY_GROUP = QName.createQName(DATALISTAUTHORITY_NAMESPACE, "group");
	
	QName PROP_DATALISTAUTHORITY_GROUP_NAME = QName.createQName(DATALISTAUTHORITY_NAMESPACE, "groupName");
	
	QName ASSOC_DATALISTAUTHORITY = QName.createQName(DATALISTAUTHORITY_NAMESPACE, "authorityMember");
	
	QName PROP_DATALISTAUTHORITY_NAME = QName.createQName(DATALISTAUTHORITY_NAMESPACE, "name");
}
