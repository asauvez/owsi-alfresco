package fr.openwide.alfresco.repo.module.classification.model;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;

public interface ClassificationPolicy<T extends ContainerModel> {

	/**
	 * Permet à la policy de choisir ce qui doit être chargé de la node.
	 * Par défaut sont chargées les propriétés de cm:object et du modèle concernée.
	 */
	void initNodeScopeBuilder(NodeScopeBuilder nodeScopeBuilder);

	/**
	 * Défini la politique de classement pour un type ou aspect donné.
	 * 
	 * @param builder Utilitaire permettant de construire la classification.
	 * @param model Le modèle de type ou d'aspect pour laquelle la politique s'applique.
	 * @param event Objet regroupant la node, le mode de classification et autres informations nécessaires.
	 */
	void classify(ClassificationBuilder builder, T model, ClassificationEvent event);

}
