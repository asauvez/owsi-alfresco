package fr.openwide.alfresco.repo.module.classification.model.builder;

import org.apache.commons.io.FilenameUtils;

/**
 * Stratégie de génération de nom unique.
 */
public class UniqueNameGenerator {
	
	protected int index = 1;

	public String generateNextName(String originalName) {
		String baseName = FilenameUtils.removeExtension(originalName);
		String extension = FilenameUtils.getExtension(originalName);
		if (! extension.isEmpty()) {
			extension = "." + extension;
		}
		
		return baseName + "-" + (index ++) + extension;
	}
}
