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
	 * Renvoi un Optional Empty si on a déjà le bon nom
	 */
	Optional<String> getUniqueValidName(String newName, NodeRef parentNode, Optional<NodeRef> currentNode);
	Optional<String> getUniqueValidName(String newName, Collection<NodeRef> parentNodes, Optional<NodeRef> currentNode);
	Optional<String> getUniqueValidName(String newName, Collection<NodeRef> parentNodes, Optional<NodeRef> currentNode,
			UniqueNameGenerator nameGenerator);

}
