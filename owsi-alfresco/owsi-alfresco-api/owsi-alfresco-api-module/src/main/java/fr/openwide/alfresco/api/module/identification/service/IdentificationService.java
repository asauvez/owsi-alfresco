package fr.openwide.alfresco.api.module.identification.service;

import com.google.common.base.Optional;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;

public interface IdentificationService {

	Optional<NodeReference> getByIdentifier(NameReference identifier);

	void setIdentifier(NodeReference nodeReference, NameReference identifier);

}
