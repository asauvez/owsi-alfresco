package fr.openwide.alfresco.repo.core.bootstrap.builder;

import org.alfresco.service.cmr.repository.NodeRef;

import fr.openwide.alfresco.repo.core.bootstrap.service.impl.BootstrapServiceImpl;

public class FolderBootstrap extends AbstractNodeBootstrap<FolderBootstrap> {
	
	public FolderBootstrap(NodeRef nodeRef, BootstrapServiceImpl bootstrapService) {
		super(nodeRef, bootstrapService);
	}
	
	public FolderBootstrap subFolder(String newFolderName) {
		return new FolderBootstrap(getNodeService().getOrCreateFolder(nodeRef, newFolderName), bootstrapService);
	}
	
	public FileBootstrap importFileFromClassPath(String fileName) {
		return new FileBootstrap(bootstrapService.importFileFromClassPath(nodeRef, fileName), bootstrapService);
	}
	
	public FolderBootstrap generateRandomFiles(RandomFileGenerator builder) {
		bootstrapService.generateRandomFiles(nodeRef, builder);
		return this;
	}
}
