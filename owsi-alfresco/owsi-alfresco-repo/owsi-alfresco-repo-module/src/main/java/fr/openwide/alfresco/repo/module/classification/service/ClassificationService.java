package fr.openwide.alfresco.repo.module.classification.service;

import java.util.function.Consumer;

import org.alfresco.service.cmr.repository.NodeRef;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.component.model.search.model.restriction.Restriction;
import fr.openwide.alfresco.repo.module.classification.model.builder.ClassificationBuilder;
import fr.openwide.alfresco.repo.module.classification.model.policy.ClassificationPolicy;

/**
 * Permet de déplacer des node dans un système de classification.
 * Les nodes doivent avoir l'aspect owsi:classifiable.
 * Un service métier doit appeler ce service en lui fournissant un ClassificationPolicy.
 */
public interface ClassificationService {

	void autoClassification(ContainerModel containerModel);
	<T extends ContainerModel> void addClassification(T model, ClassificationPolicy<T> policy);
	<T extends ContainerModel> void addClassification(T model, Consumer<ClassificationBuilder> builder);
	
	int DEFAULT_RECLASSIFY_BATCH_SIZE = 100;

	int reclassifyAll(Integer batchSize);
	int reclassify(ContainerModel model, Integer batchSize, Restriction ...restrictions);
	int reclassify(NameReference modelName, Integer batchSize);
	
	void classify(NodeRef nodeRef);
	
	void clearCaches();
	
	void registerTreeAspect(ContainerModel container);
}
