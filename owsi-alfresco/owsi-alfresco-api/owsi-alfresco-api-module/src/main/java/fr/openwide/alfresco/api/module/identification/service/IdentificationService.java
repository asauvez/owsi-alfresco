package fr.openwide.alfresco.api.module.identification.service;

import java.util.List;

import java.util.Optional;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;

public interface IdentificationService {

	Optional<NodeReference> getByIdentifier(NameReference identifier);
	Optional<BusinessNode> getByIdentifier(NameReference identifier, NodeScopeBuilder nodeScopeBuilder);

	List<NodeReference> listByIdentifier(NameReference identifier);
	List<BusinessNode> listByIdentifier(NameReference identifier, NodeScopeBuilder nodeScopeBuilder);

	void setIdentifier(NodeReference nodeReference, NameReference identifier);

}
