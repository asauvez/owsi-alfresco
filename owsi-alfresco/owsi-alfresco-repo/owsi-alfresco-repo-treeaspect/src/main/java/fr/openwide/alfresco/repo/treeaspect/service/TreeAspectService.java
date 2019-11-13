package fr.openwide.alfresco.repo.treeaspect.service;

import fr.openwide.alfresco.repo.treeaspect.service.impl.TreeAspectServiceImpl;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface TreeAspectService {

	void registerAspect(QName aspect);

	void registerAspect(QName aspect, boolean breakIneritanceDuringMove);
}
