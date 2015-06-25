package fr.openwide.alfresco.component.model.node.model.embed;

import fr.openwide.alfresco.api.core.node.model.RepositoryContentData;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.property.single.ContentPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class ContentsNode {

	private final BusinessNode node;
	private final RepositoryNode repoNode;
	
	public ContentsNode(BusinessNode node) {
		this.node = node;
		this.repoNode = node.getRepositoryNode();
	}

	@SuppressWarnings("unchecked")
	public <T> T get() {
		return (T) get(CmModel.content.content);
	}
	@SuppressWarnings("unchecked")
	public <T> T get(ContentPropertyModel propertyModel) {
		return (T) repoNode.getContents().get(propertyModel.getNameReference());
	}
	public BusinessNode set(Object content) {
		return set(CmModel.content.content, content);
	}
	public BusinessNode set(ContentPropertyModel property, Object content) {
		return set(property, null, content);
	}
	public BusinessNode set(RepositoryContentData contentData, Object content) {
		return set(CmModel.content.content, contentData, content);
	}
	public BusinessNode set(ContentPropertyModel property, RepositoryContentData contentData, Object content) {
		if (content == null) {
			content = new byte[0];
		}
		repoNode.getContents().put(property.getNameReference(), content);
		if (contentData != null) {
			repoNode.getProperties().put(property.getNameReference(), contentData);
		}
		return node;
	}
	
	public RepositoryContentData getData() {
		return node.properties().get(CmModel.content.content);
	}
}
