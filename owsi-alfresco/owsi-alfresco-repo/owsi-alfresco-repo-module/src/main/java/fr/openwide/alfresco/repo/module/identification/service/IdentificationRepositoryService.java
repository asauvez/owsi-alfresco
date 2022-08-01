package fr.openwide.alfresco.repo.module.identification.service;

import java.util.List;
import java.util.Optional;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

public interface IdentificationRepositoryService {

	Optional<NodeRef> getByIdentifier(QName identifier);

	List<NodeRef> listByIdentifier(QName identifier);

	void setIdentifier(NodeRef nodeRef, QName identifier);

}
