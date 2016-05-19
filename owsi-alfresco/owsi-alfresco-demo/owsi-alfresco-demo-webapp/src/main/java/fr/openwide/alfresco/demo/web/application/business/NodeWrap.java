package fr.openwide.alfresco.demo.web.application.business;

import java.util.ArrayList;
import java.util.List;

import fr.openwide.alfresco.api.core.node.model.RepositoryPermission;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class NodeWrap {
	
	private final BusinessNode node;
	
	public NodeWrap(BusinessNode node) {
		this.node = node;
	}
	
	public String getName() {
		return node.properties().getName();
	}
	
	public long getFileSize() {
		return node.properties().get(CmModel.content.content).getSize();
	}
	
	public String getTitle() {
		return node.properties().getTitle();
	}
	
	public String getDescription() {
		return node.properties().getDescription();
	}
	
	public NodeReference getNodeRef() {
		return node.getNodeReference();
	}
	
	public boolean isFolder() {
		return node.properties().get(CmModel.content.content) == null;
	}
	
	public boolean isMayDelete() {
		return node.permissions().hasUserPermissionDelete();
	}
	
	public boolean isMayAdd() {
		return node.permissions().hasUserPermissionAddChildren();
	}
	
	public List<NodeWrap> getChildren(){
		List<BusinessNode> temp = node.assocs().getChildAssociationContains();
		List<NodeWrap> tmp = new ArrayList<NodeWrap>();
		
		for (BusinessNode bn : temp){
			tmp.add(new NodeWrap(bn));
		}
		
		return tmp;
	}
	
	public String getPath() {
		return node.getPath();
	}
	
	public NodeReference getParent(){
		return node.assocs().getPrimaryParent().getNodeReference();
	}
	
	public String getMime() {
		return node.properties().get(CmModel.content.content).getMimetype();
	}
}
