package fr.openwide.alfresco.repo.core.bootstrap.builder;

import java.io.Serializable;

import org.alfresco.service.cmr.repository.NodeRef;

import fr.openwide.alfresco.api.core.authority.model.AuthorityReference;
import fr.openwide.alfresco.api.core.node.model.PermissionReference;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.bean.NodeBean;
import fr.openwide.alfresco.component.model.node.model.property.single.EnumTextPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.SinglePropertyModel;
import fr.openwide.alfresco.repo.core.bootstrap.service.impl.BootstrapServiceImpl;
import fr.openwide.alfresco.repo.dictionary.node.service.NodeModelRepositoryService;

public class AbstractNodeBootstrap<T extends AbstractNodeBootstrap<T>> {
	
	protected NodeRef nodeRef;
	protected BootstrapServiceImpl bootstrapService;

	public AbstractNodeBootstrap(NodeRef nodeRef, BootstrapServiceImpl bootstrapService) {
		this.nodeRef = nodeRef;
		this.bootstrapService = bootstrapService;
	}
	
	public NodeRef getNodeRef() {
		return nodeRef;
	}

	protected NodeModelRepositoryService getNodeService() {
		return bootstrapService.getNodeModelRepositoryService();
	}

	public FolderBootstrap parent() {
		return new FolderBootstrap(getNodeService().getPrimaryParent(nodeRef).get(), bootstrapService);
	}
	@SuppressWarnings("unchecked")
	private T getThis() {
		return (T) this;
	}
	public T aspect(AspectModel aspect) {
		getNodeService().addAspect(nodeRef, aspect);
		return getThis();
	}
	
	public <C extends Serializable> T property(SinglePropertyModel<C> property, C value) {
		getNodeService().setProperty(nodeRef, property, value);
		return getThis();
	}
	public <E extends Enum<E>> T property(EnumTextPropertyModel<E> property, E value) {
		getNodeService().setProperty(nodeRef, property, value);
		return getThis();
	}
	public T properties(NodeBean bean) {
		getNodeService().setProperties(nodeRef, bean);
		return getThis();
	}
	
	public T inheritParentPermissions(boolean inheritParentPermissions) {
		bootstrapService.getPermissionRepositoryService().setInheritParentPermissions(nodeRef, inheritParentPermissions);
		return getThis();
	}
	public T permission(AuthorityReference authority, PermissionReference permission) {
		bootstrapService.getPermissionRepositoryService().setPermission(nodeRef, authority, permission);
		return getThis();
	}

}
