package fr.openwide.alfresco.repo.module.classification.model.policy;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.repo.module.classification.model.ClassificationEvent;
import fr.openwide.alfresco.repo.module.classification.model.builder.ClassificationBuilder;

public interface ClassificationPolicy<T extends ContainerModel> {

	/**
	 * Défini la politique de classement pour un type ou aspect donné.
	 * 
	 * @param builder Utilitaire permettant de construire la classification.
	 * @param model Le modèle de type ou d'aspect pour laquelle la politique s'applique.
	 * @param event Objet regroupant la node, le mode de classification et autres informations nécessaires.
	 */
	void classify(ClassificationBuilder builder, T model, ClassificationEvent event);

}
