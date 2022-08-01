package fr.openwide.alfresco.repo.module.identification.service.impl;

import java.util.List;
import java.util.Optional;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.springframework.beans.factory.annotation.Autowired;

import fr.openwide.alfresco.component.model.search.model.restriction.RestrictionBuilder;
import fr.openwide.alfresco.repo.dictionary.node.service.NodeModelRepositoryService;
import fr.openwide.alfresco.repo.dictionary.search.service.NodeSearchModelRepositoryService;
import fr.openwide.alfresco.repo.module.OwsiModel;
import fr.openwide.alfresco.repo.module.identification.service.IdentificationRepositoryService;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateService;

@GenerateService
public class IdentificationRepositoryServiceImpl implements IdentificationRepositoryService {

	@Autowired private NodeSearchModelRepositoryService nodeSearchModelService;
	@Autowired private NodeModelRepositoryService nodeModelService;
	
	@Override
	public Optional<NodeRef> getByIdentifier(QName identifier) {
		return nodeSearchModelService.searchReferenceUnique(new RestrictionBuilder()
				.eq(OwsiModel.identifiable.identifier, identifier).of());
	}

	@Override
	public List<NodeRef> listByIdentifier(QName identifier) {
		return nodeSearchModelService.searchReference(new RestrictionBuilder()
				.eq(OwsiModel.identifiable.identifier, identifier).of());
	}

	@Override
	public void setIdentifier(NodeRef nodeRef, QName identifier) {
		nodeModelService.setProperty(nodeRef, OwsiModel.identifiable.identifier, identifier);
	}
}
