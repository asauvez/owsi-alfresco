package fr.openwide.alfresco.repo.dictionary.node.service;

import java.util.Optional;

import org.alfresco.service.cmr.repository.NodeRef;

public interface UniqueNameRepositoryService {

	String toValidName(String nodeName);

	String getUniqueValidName(String newName, NodeRef parentNode);

	/**
	 * Renvoi un Optional Empty si on a déjà le bon nom
	 */
	Optional<String> getUniqueValidName(String newName, NodeRef parentNode, Optional<NodeRef> currentNode);

}
