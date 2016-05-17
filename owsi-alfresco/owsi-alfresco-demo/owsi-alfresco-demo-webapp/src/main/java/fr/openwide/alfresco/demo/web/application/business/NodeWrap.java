package fr.openwide.alfresco.demo.web.application.business;

import java.util.ArrayList;
import java.util.List;

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
	
	public List<NodeWrap> getChildren(){
		List<BusinessNode> temp = node.assocs().getChildAssociationContains();
		List<NodeWrap> tmp = new ArrayList<NodeWrap>();
		
		for (BusinessNode bn : temp){
			tmp.add(new NodeWrap(bn));
		}
		
		return tmp;
	}
}
