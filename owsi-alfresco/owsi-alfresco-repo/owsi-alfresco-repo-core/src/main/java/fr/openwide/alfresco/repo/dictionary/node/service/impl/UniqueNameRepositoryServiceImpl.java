package fr.openwide.alfresco.repo.dictionary.node.service.impl;

import java.util.Optional;

import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.FileNameValidator;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import fr.openwide.alfresco.repo.dictionary.node.service.UniqueNameRepositoryService;

public class UniqueNameRepositoryServiceImpl implements UniqueNameRepositoryService {
	private final Logger logger = LoggerFactory.getLogger(UniqueNameRepositoryServiceImpl.class);
	
	@Autowired
	private FileFolderService fileFolderService;
	
	@Override
	public String toValidName(String nodeName) {
		String validName = FileNameValidator.getValidFileName(nodeName);
    	while (validName.endsWith(" ") || validName.endsWith(".")) {
    		validName = validName.substring(0, validName.length() - 1);
    	}
    	
    	if (!nodeName.equals(validName)) {
    		logger.trace("UniqueNameRepositoryService.toValidName : Old name = '" + nodeName + "', new name = " + validName);
    	}
    	return validName;
	}
	
	@Override
	public String getUniqueValidName(String newName, NodeRef parentNode) {
		return getUniqueValidName(newName, parentNode, Optional.empty()).get();
	}
	
	/**
	 * Renvoi un Optional Empty si on a déjà le bon nom
	 */
	@Override
	public Optional<String> getUniqueValidName(String newName, NodeRef parentNode, Optional<NodeRef> currentNode) {
		String validNewName = toValidName(newName);
		
		int i = 0;
		String extension = FilenameUtils.getExtension(validNewName);
		if (StringUtils.isNotEmpty(extension)) {
			extension = "." + extension;
		}
		String baseName = FilenameUtils.removeExtension(validNewName);
		
		NodeRef existingNode = fileFolderService.searchSimple(parentNode, validNewName);
		while (existingNode != null) {
			if (currentNode.isPresent() && existingNode.equals(currentNode.get())) {
				// On est déjà dans le bon dossier avec le bon nom (ou renommer avec un -i et on ne trouvera pas plus petit comme nom)
				return Optional.empty();
			}
			i++ ;
			
			validNewName = baseName + "-" + i + extension;
			
			existingNode = fileFolderService.searchSimple(parentNode, validNewName);
		}
		
		return Optional.of(validNewName);
	}
}
