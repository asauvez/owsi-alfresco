package fr.openwide.alfresco.component.model.node.model.embed;

import fr.openwide.alfresco.api.core.node.model.RenditionsSetter;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;

public class RenditionsNode extends RenditionsSetter<BusinessNode> {

	private final RepositoryNode repoNode;
	
	public RenditionsNode(BusinessNode node) {
		this.repoNode = node.getRepositoryNode();
	}

	@Override
	public BusinessNode name(NameReference renditionName) {
		RepositoryNode renditionNode = repoNode.getRenditions().get(renditionName);
		return (renditionNode != null) ? new BusinessNode(renditionNode) : null;
	}

}
