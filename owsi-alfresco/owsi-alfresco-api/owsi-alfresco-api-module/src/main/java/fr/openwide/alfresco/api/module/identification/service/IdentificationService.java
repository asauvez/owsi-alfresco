package fr.openwide.alfresco.api.module.identification.service;

import com.google.common.base.Optional;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;

public interface IdentificationService {

	Optional<NodeReference> getByIdentifier(NameReference identifier);
	Optional<BusinessNode> getByIdentifier(NameReference identifier, NodeScopeBuilder nodeScopeBuilder);

	void setIdentifier(NodeReference nodeReference, NameReference identifier);

}
