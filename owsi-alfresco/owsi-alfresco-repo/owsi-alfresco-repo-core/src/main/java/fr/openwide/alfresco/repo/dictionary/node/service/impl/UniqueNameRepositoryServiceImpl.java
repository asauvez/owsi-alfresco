package fr.openwide.alfresco.repo.dictionary.node.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.FileNameValidator;
import org.springframework.beans.factory.annotation.Autowired;

import fr.openwide.alfresco.repo.dictionary.node.service.UniqueNameRepositoryService;

public class UniqueNameRepositoryServiceImpl implements UniqueNameRepositoryService {
	
	@Autowired
	private FileFolderService fileFolderService;
	
	@Override
	public String toValidName(String nodeName) {
		return toValidName(nodeName, "_");
	}
	
	@Override
	public String toValidName(String nodeName, String replacementStr) {
		String validName = nodeName.replaceAll(FileNameValidator.FILENAME_ILLEGAL_REGEX, replacementStr);
		while (validName.endsWith(" ") || validName.endsWith(".")) {
			validName = validName.substring(0, validName.length() - 1);
		}

		return validName;
	}
	
	@Override
	public String getUniqueValidName(String newName, NodeRef parentNode) {
		return getUniqueValidName(newName, Collections.singletonList(parentNode), Optional.empty(), new UniqueNameGenerator()).get();
	}
	@Override
	public String getUniqueValidName(String newName, NodeRef parentNode, UniqueNameGenerator nameGenerator) {
		return getUniqueValidName(newName, Collections.singletonList(parentNode), Optional.empty(), nameGenerator).get();
	}
	
	/**
	 * Renvoi un Optional Empty si on a déjà le bon nom
	 */
	@Override
	public Optional<String> getUniqueValidName(String newName, NodeRef parentNode, Optional<NodeRef> currentNode) {
		return getUniqueValidName(newName, Collections.singletonList(parentNode), currentNode, new UniqueNameGenerator());
	}
	@Override
	public Optional<String> getUniqueValidName(String newName, Collection<NodeRef> parentNodes, Optional<NodeRef> currentNode) {
		return getUniqueValidName(newName, parentNodes, currentNode, new UniqueNameGenerator());
	}
	@Override
	public Optional<String> getUniqueValidName(String newName, Collection<NodeRef> parentNodes, Optional<NodeRef> currentNode, UniqueNameGenerator nameGenerator) {
		String originalValidNewName = toValidName(newName);
		String validNewName = originalValidNewName;
		
		List<NodeRef> existingNodes = getExistingNodes(validNewName, parentNodes);
		
		while (!existingNodes.isEmpty()) {
			if (existingNodes.size() == 1 && existingNodes.get(0).equals(currentNode.get())) {
				// On est déjà dans le bon dossier avec le bon nom (ou renommer avec un -i et on ne trouvera pas plus petit comme nom)
				return Optional.empty();
			}
			
			validNewName = nameGenerator.generateNextName(originalValidNewName);
			
			existingNodes = getExistingNodes(validNewName, parentNodes);
		}
		
		return Optional.of(validNewName);
	}
	
	private List<NodeRef> getExistingNodes(String nodeName, Collection<NodeRef> folders) {
		List<NodeRef> result = new ArrayList<NodeRef>();
		for (NodeRef parentNode : folders) {
			NodeRef existingNode = fileFolderService.searchSimple(parentNode, nodeName);
			if (existingNode != null) {
				result.add(existingNode);
			}
		}
		
		return result;
	}
}
