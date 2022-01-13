package fr.openwide.alfresco.repo.module.identification.service;

import java.util.List;
import java.util.Optional;

import org.alfresco.service.cmr.repository.NodeRef;

import fr.openwide.alfresco.api.core.remote.model.NameReference;

public interface IdentificationRepositoryService {

	Optional<NodeRef> getByIdentifier(NameReference identifier);

	List<NodeRef> listByIdentifier(NameReference identifier);

	void setIdentifier(NodeRef nodeRef, NameReference identifier);

}
