package fr.openwide.alfresco.api.module.identification.service.impl;

import com.google.common.base.Optional;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.api.module.identification.service.IdentificationService;
import fr.openwide.alfresco.api.module.model.OwsiModel;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.component.model.node.service.NodeModelService;
import fr.openwide.alfresco.component.model.search.model.restriction.RestrictionBuilder;
import fr.openwide.alfresco.component.model.search.service.NodeSearchModelService;

public class IdentificationServiceImpl implements IdentificationService {

	private NodeSearchModelService nodeSearchModelService;
	private NodeModelService nodeModelService;
	
	public IdentificationServiceImpl(NodeSearchModelService nodeSearchModelService, NodeModelService nodeModelService) {
		this.nodeSearchModelService = nodeSearchModelService;
		this.nodeModelService = nodeModelService;
	}
	
	@Override
	public Optional<NodeReference> getByIdentifier(NameReference identifier) {
		return nodeSearchModelService.searchUniqueReference(new RestrictionBuilder()
				.eq(OwsiModel.identifiable.identifier, identifier).of());
	}

	@Override
	public Optional<BusinessNode> getByIdentifier(NameReference identifier, NodeScopeBuilder nodeScopeBuilder) {
		return nodeSearchModelService.searchUnique(new RestrictionBuilder()
				.eq(OwsiModel.identifiable.identifier, identifier).of(),
				nodeScopeBuilder);
	}

	@Override
	public void setIdentifier(NodeReference nodeReference, NameReference identifier) {
		nodeModelService.update(new BusinessNode(nodeReference)
				.properties().set(OwsiModel.identifiable.identifier, identifier),
			new NodeScopeBuilder()
				.properties().set(OwsiModel.identifiable.identifier));
	}
}
