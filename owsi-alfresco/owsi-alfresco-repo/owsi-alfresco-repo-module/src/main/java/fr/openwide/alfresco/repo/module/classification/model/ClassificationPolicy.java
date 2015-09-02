package fr.openwide.alfresco.repo.module.classification.model;

import fr.openwide.alfresco.component.model.node.model.BusinessNode;
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
	 * @param node La node concernée avec les informations demandé dans le NodeScopeBuilder.
	 * @param update Vrai si la node était déjà classé.
	 */
	void classify(ClassificationBuilder builder, T model, BusinessNode node, boolean update);

}
