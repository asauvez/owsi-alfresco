package fr.openwide.alfresco.component.model.node.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;

/**
 * Wrappe une liste de RepositoryNode dans une liste de BusinessNode.
 * 
 * Les modifications sur cette liste seront repris sur la list wrappé.
 * 
 * @author asauvez
 */
public class BusinessNodeList extends ArrayList<BusinessNode> {
	
	private final List<RepositoryNode> nodes;
	
	public BusinessNodeList(List<RepositoryNode> nodes) {
		this.nodes = nodes;
		for (RepositoryNode node : nodes) {
			super.add(new BusinessNode(node));
		}
	}

	@Override
	public boolean add(BusinessNode node) {
		nodes.add(node.getRepositoryNode());
		return super.add(node);
	}
	@Override
	public boolean addAll(Collection<? extends BusinessNode> c) {
		for (BusinessNode node : c) {
			nodes.add(node.getRepositoryNode());
		}
		return super.addAll(c);
	}
	@Override
	public BusinessNode remove(int index) {
		nodes.remove(index);
		return super.remove(index);
	}
	@Override
	public boolean remove(Object o) {
		nodes.remove(o);
		return super.remove(o);
	}
	@Override
	public boolean removeAll(Collection<?> c) {
		for (Object node : c) {
			nodes.remove(((BusinessNode) node).getRepositoryNode());
		}
		return super.removeAll(c);
	}
	@Override
	public void clear() {
		nodes.clear();
		super.clear();
	}

}
