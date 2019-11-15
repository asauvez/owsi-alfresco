package fr.openwide.alfresco.repo.treeaspect.service;

import org.alfresco.service.namespace.QName;

public interface TreeAspectService {

	void registerAspect(QName aspect);

	void registerAspect(QName aspect, boolean breakInheritanceDuringMove);
}
