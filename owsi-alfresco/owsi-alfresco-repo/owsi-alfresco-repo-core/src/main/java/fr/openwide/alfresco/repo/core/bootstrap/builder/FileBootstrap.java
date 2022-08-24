package fr.openwide.alfresco.repo.core.bootstrap.builder;

import org.alfresco.service.cmr.repository.NodeRef;

import fr.openwide.alfresco.repo.core.bootstrap.service.impl.BootstrapServiceImpl;

public class FileBootstrap extends AbstractNodeBootstrap<FileBootstrap> {

	public FileBootstrap(NodeRef nodeRef, BootstrapServiceImpl bootstrapService) {
		super(nodeRef, bootstrapService);
	}
	
}
