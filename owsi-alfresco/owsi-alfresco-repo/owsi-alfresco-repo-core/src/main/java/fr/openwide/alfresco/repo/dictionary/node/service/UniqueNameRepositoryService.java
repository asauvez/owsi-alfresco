package fr.openwide.alfresco.repo.dictionary.node.service;

import java.util.Collection;
import java.util.Optional;

import org.alfresco.service.cmr.repository.NodeRef;

import fr.openwide.alfresco.repo.dictionary.node.service.impl.UniqueNameGenerator;

public interface UniqueNameRepositoryService {

	String toValidName(String nodeName);
	String toValidName(String nodeName, String replacementStr);

	String getUniqueValidName(String newName, NodeRef parentNode);
	String getUniqueValidName(String newName, NodeRef parentNode, UniqueNameGenerator nameGenerator);

	/**
	 * @param newName Nouveau nom souhaité
	 * @param parentNode Dossier dans lequel on souhaite trouver un nom disponible
	 * @param currentNode Node actuel, pour éviter de renommer si on a déjà le bon nom
	 * @return Le nouveau nom, ou Optional.empty() si on est déjà dans le dossier avec le bon nom
	 */
	Optional<String> getUniqueValidName(String newName, NodeRef parentNode, Optional<NodeRef> currentNode);
	/**
	 * @param newName Nouveau nom souhaité
	 * @param parentNode Dossier dans lequel on souhaite trouver un nom disponible
	 * @param currentNode Node actuel, pour éviter de renommer si on a déjà le bon nom
	 * @param nameGenerator Pattern pour les nouveaux noms, par défaut -1
	 * @return Le nouveau nom, ou Optional.empty() si on est déjà dans le dossier avec le bon nom
	 */
	Optional<String> getUniqueValidName(String newName, NodeRef parentNode, Optional<NodeRef> currentNode,
			UniqueNameGenerator nameGenerator);
	/**
	 * @param newName Nouveau nom souhaité
	 * @param parentNodes Dossiers dans lesquels on souhaite trouver un nom disponible
	 * @param currentNode Node actuel, pour éviter de renommer si on a déjà le bon nom
	 * @return Le nouveau nom, ou Optional.empty() si on est déjà dans le dossier avec le bon nom
	 */
	Optional<String> getUniqueValidName(String newName, Collection<NodeRef> parentNodes, Optional<NodeRef> currentNode);
	/**
	 * @param newName Nouveau nom souhaité
	 * @param parentNodes Dossiers dans lesquels on souhaite trouver un nom disponible
	 * @param currentNode Node actuel, pour éviter de renommer si on a déjà le bon nom
	 * @param nameGenerator Pattern pour les nouveaux noms, par défaut -1
	 * @return Le nouveau nom, ou Optional.empty() si on est déjà dans le dossier avec le bon nom
	 */
	Optional<String> getUniqueValidName(String newName, Collection<NodeRef> parentNodes, Optional<NodeRef> currentNode,
			UniqueNameGenerator nameGenerator);
	
	void setUniqueNodeName(NodeRef nodeRef, String newName);
	void setUniqueNodeName(NodeRef nodeRef, String newName, UniqueNameGenerator nameGenerator);
	
	void moveWithUniqueName(NodeRef nodeRef, NodeRef parentFolder);
	void moveWithUniqueName(NodeRef nodeRef, String newName, NodeRef parentFolder);
	void moveWithUniqueName(NodeRef nodeRef, String newName, NodeRef parentFolder, UniqueNameGenerator nameGenerator);
	
}
