package fr.openwide.alfresco.repo.dictionary.node.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.FileNameValidator;
import org.springframework.beans.factory.annotation.Autowired;

import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.repo.dictionary.node.service.NodeModelRepositoryService;
import fr.openwide.alfresco.repo.dictionary.node.service.UniqueNameRepositoryService;

public class UniqueNameRepositoryServiceImpl implements UniqueNameRepositoryService {
	
	@Autowired
	private FileFolderService fileFolderService;
	@Autowired
	private NodeModelRepositoryService nodeModelRepositoryService;
	
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
	
	@Override
	public Optional<String> getUniqueValidName(String newName, NodeRef parentNode, Optional<NodeRef> currentNode) {
		return getUniqueValidName(newName, Collections.singletonList(parentNode), currentNode, new UniqueNameGenerator());
	}
	@Override
	public Optional<String> getUniqueValidName(String newName, NodeRef parentNode, Optional<NodeRef> currentNode, 
			UniqueNameGenerator nameGenerator) {
		return getUniqueValidName(newName, Collections.singletonList(parentNode), currentNode, nameGenerator);
	}
	@Override
	public Optional<String> getUniqueValidName(String newName, Collection<NodeRef> parentNodes, Optional<NodeRef> currentNode) {
		return getUniqueValidName(newName, parentNodes, currentNode, new UniqueNameGenerator());
	}
	@Override
	public Optional<String> getUniqueValidName(String newName, Collection<NodeRef> parentNodes, Optional<NodeRef> currentNode, 
			UniqueNameGenerator nameGenerator) {
		String originalValidNewName = toValidName(newName);
		String validNewName = originalValidNewName;
		
		Set<NodeRef> existingNodes = getExistingNodes(validNewName, parentNodes);
		
		while (!existingNodes.isEmpty()) {
			if (currentNode.isPresent() && existingNodes.size() == 1 && existingNodes.contains(currentNode.get())) {
				// On est déjà dans le bon dossier avec le bon nom (ou renommé avec un -i et on ne trouvera pas plus petit comme nom)
				return Optional.empty();
			}
			
			validNewName = nameGenerator.generateNextName(originalValidNewName);
			
			existingNodes = getExistingNodes(validNewName, parentNodes);
		}
		
		return Optional.of(validNewName);
	}
	
	@Override
	public void setUniqueNodeName(NodeRef nodeRef, String newName) {
		setUniqueNodeName(nodeRef, newName, new UniqueNameGenerator());
	}
	
	@Override
	public void setUniqueNodeName(NodeRef nodeRef, String newName, UniqueNameGenerator nameGenerator) {
		List<NodeRef> parentsNodes = nodeModelRepositoryService.getParentAssocs(nodeRef);
		if (parentsNodes.isEmpty()) {
			throw new IllegalStateException("Can't rename node without parent : " + nodeRef);
		}
		
		Optional<String> validUniqueNewName = getUniqueValidName(newName, parentsNodes, Optional.of(nodeRef), nameGenerator);
		if (!validUniqueNewName.isPresent()) {
			// On a déjà le bon nom, pas besoin de renommer
			return ;
		}
		
		try {
			fileFolderService.rename(nodeRef, validUniqueNewName.get());
		} catch (FileExistsException | FileNotFoundException e) {
			throw new IllegalStateException("Exception while removing node " + nodeRef + " : " + validUniqueNewName.get(), e);
		}
	}
	
	@Override
	public void moveWithUniqueName(NodeRef nodeRef, NodeRef parentFolder) {
		moveWithUniqueName(nodeRef, nodeModelRepositoryService.getProperty(nodeRef, CmModel.object.name), 
				parentFolder, new UniqueNameGenerator());
	}
	
	@Override
	public void moveWithUniqueName(NodeRef nodeRef, String newName, NodeRef parentFolder) {
		moveWithUniqueName(nodeRef, newName, parentFolder, new UniqueNameGenerator());
	}
	
	@Override
	public void moveWithUniqueName(NodeRef nodeRef, String newName, NodeRef parentFolder, UniqueNameGenerator nameGenerator) {
		Optional<NodeRef> currentParent = nodeModelRepositoryService.getPrimaryParent(nodeRef);
		if (currentParent.isPresent() && currentParent.get().equals(parentFolder)) {
			// Déjà dans le bon dossier, on renomme juste
			setUniqueNodeName(nodeRef, newName, nameGenerator);
			return ;
		}
		
		String uniqueNewName = getUniqueValidName(newName, parentFolder, nameGenerator);
		try {
			fileFolderService.move(nodeRef, parentFolder, uniqueNewName);
		} catch (FileExistsException | FileNotFoundException e) {
			throw new IllegalStateException("Exception while moving node " + nodeRef + "  to " + parentFolder + " with name : " + uniqueNewName, e);
		}
	}
	
	private Set<NodeRef> getExistingNodes(String nodeName, Collection<NodeRef> folders) {
		Set<NodeRef> result = new HashSet<NodeRef>();
		for (NodeRef parentNode : folders) {
			nodeModelRepositoryService.getChildByName(parentNode, nodeName).ifPresent(existingNode -> result.add(existingNode));
		}
		
		return result;
	}
}
