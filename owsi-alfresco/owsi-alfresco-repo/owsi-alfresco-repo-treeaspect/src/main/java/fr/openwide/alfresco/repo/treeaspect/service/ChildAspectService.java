package fr.openwide.alfresco.repo.treeaspect.service;

import java.util.function.Consumer;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

public interface ChildAspectService {

	void registerChildAspectForFolder(QName parentAspect, QName childAspect);
	void registerChildAspectForContent(QName parentAspect, QName childAspect);
	void registerChildAspectForFolder(QName parentAspect, Consumer<NodeRef> consumer);
	void registerChildAspectForContent(QName parentAspect, Consumer<NodeRef> consumer);
}
