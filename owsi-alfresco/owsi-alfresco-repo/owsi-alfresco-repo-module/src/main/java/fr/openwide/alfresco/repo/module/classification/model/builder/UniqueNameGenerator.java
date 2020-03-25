package fr.openwide.alfresco.repo.module.classification.model.builder;

import org.apache.commons.io.FilenameUtils;

/**
 * Stratégie de génération de nom unique.
 */
public class UniqueNameGenerator {
	
	protected int index = 0;

	public String generateNextName(String originalName) {
		String baseName = FilenameUtils.removeExtension(originalName);
		String extension = FilenameUtils.getExtension(originalName);
		if (! extension.isEmpty()) {
			extension = "." + extension;
		}
		
		index ++;
		return generateNextName(baseName, extension, index);
	}
	
	protected String generateNextName(String baseName, String extension, int index) {
		//return baseName + " (" + index + ")" + extension;
		return baseName + "-" + index + extension;
	}
}
