package fr.openwide.alfresco.api.core.node.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import fr.openwide.alfresco.api.core.node.model.RepositoryVisitor.RepositoryVisitable;
import fr.openwide.alfresco.api.core.remote.model.NameReference;


public abstract class RepositoryVisitor<T extends RepositoryVisitable<T>> {

	public static interface RepositoryVisitable<T extends RepositoryVisitable<T>> {
		void visit(RepositoryVisitor<T> visitor);
	}

	private List<Object> currentPath = new ArrayList<>();
	
	public abstract void visit(T item);
	
	protected List<Object> getCurrentPath() {
		return new ArrayList<>(currentPath);
	}
	
	protected void push(String state) {
		currentPath.add(state);
	}
	protected void push(NameReference state) {
		currentPath.add(state);
	}
	protected void pop(String state) {
		if (! state.equals(currentPath.remove(currentPath.size()-1))) {
			throw new IllegalStateException();
		}
	}
	protected void pop(NameReference state) {
		if (! state.equals(currentPath.remove(currentPath.size()-1))) {
			throw new IllegalStateException();
		}
	}
	
	public void visitMap(String name, Map<NameReference, T> map) {
		if (! map.isEmpty()) {
			push(name);
			for (Entry<NameReference, T> entry : map.entrySet()) {
				push(entry.getKey());
				entry.getValue().visit(this);
				pop(entry.getKey());
			}
			pop(name);
		}
	}
	public void visitMapList(String name, Map<NameReference, List<T>> map) {
		if (! map.isEmpty()) {
			push(name);
			for (Entry<NameReference, List<T>> entry : map.entrySet()) {
				push(entry.getKey());
				for (T item : entry.getValue()) {
					item.visit(this);
				}
				pop(entry.getKey());
			}
			pop(name);
		}
	}
}
