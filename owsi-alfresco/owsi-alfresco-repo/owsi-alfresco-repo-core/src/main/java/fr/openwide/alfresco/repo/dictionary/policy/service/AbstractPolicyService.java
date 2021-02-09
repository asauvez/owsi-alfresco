package fr.openwide.alfresco.repo.dictionary.policy.service;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alfresco.repo.policy.AssociationPolicy;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.ClassPolicy;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.EnumTextPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.SinglePropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.repo.dictionary.node.service.NodeModelRepositoryService;
import fr.openwide.alfresco.repo.remote.conversion.service.ConversionService;

/**
 * Classe mère pour mettre en place des policy pour un type/aspect donnée.
 * Il suffit d'implementer une interface (comme OnUpdatePropertiesPolicy) pour y être abonné.
 */
public abstract class AbstractPolicyService implements InitializingBean {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired protected PolicyRepositoryService policyRepositoryService;
	@Autowired protected NodeModelRepositoryService nodeRepositoryService;
	@Autowired protected ConversionService conversionService;
	
	private ContainerModel model;

	public AbstractPolicyService(ContainerModel model) {
		this.model = model;
	}
	
	public NotificationFrequency getNotificationFrequency() {
		return NotificationFrequency.TRANSACTION_COMMIT;
	}
	protected ChildAssociationModel getChildAssociationModel() {
		return CmModel.folder.contains;
	}
	
	private <T extends ClassPolicy> void bindClassBehaviour(Class<T> interface_) {
		policyRepositoryService.bindClassBehaviour(model, getNotificationFrequency(), interface_, getProxyCheckExist(interface_));
	}
	private <T extends AssociationPolicy> void bindAssociationBehaviour(Class<T> interface_) {
		policyRepositoryService.bindAssociationBehaviour(model, getChildAssociationModel(), getNotificationFrequency(), interface_, getProxyCheckExist(interface_));
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void afterPropertiesSet() throws Exception {
		for (Class<?> interface_ : this.getClass().getInterfaces()) {
			if (ClassPolicy.class.isAssignableFrom(interface_)) {
				bindClassBehaviour((Class) interface_);
			} else if (AssociationPolicy.class.isAssignableFrom(interface_)) {
				bindAssociationBehaviour((Class) interface_);
			} else if (InitializingBean.class.isAssignableFrom(interface_)) {
				// ignore
			} else {
				logger.warn(interface_ + " is not a policy interface. It will be ignored");
			}
		}
	}
	
	protected boolean isCheckNodeExists() {
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private <T> T getProxyCheckExist(Class<T> interface_) {
		if (! isCheckNodeExists()) {
			return (T) this;
		}
		
		return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[] { interface_ }, new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				logger.trace("--> Beginning of " + method.getName() + "()");
				try {
					if (args != null) {
						for (Object arg : args) {
							if (arg instanceof NodeRef) {
								if (! nodeRepositoryService.exists((NodeRef) arg)) {
									logger.warn(arg + " does not exist anymore. Ignore.");
									return null;
								}
							} else if (arg instanceof ChildAssociationRef) {
								if (! nodeRepositoryService.exists(((ChildAssociationRef) arg).getParentRef())) {
									logger.warn(arg + " does not exist anymore. Ignore.");
									return null;
								}
								if (! nodeRepositoryService.exists(((ChildAssociationRef) arg).getChildRef())) {
									logger.warn(arg + " does not exist anymore. Ignore.");
									return null;
								}
							} else if (arg instanceof AssociationRef) {
								if (! nodeRepositoryService.exists(((AssociationRef) arg).getSourceRef())) {
									logger.warn(arg + " does not exist anymore. Ignore.");
									return null;
								}
								if (! nodeRepositoryService.exists(((AssociationRef) arg).getTargetRef())) {
									logger.warn(arg + " does not exist anymore. Ignore.");
									return null;
								}
							}
						}
					}
					return method.invoke(AbstractPolicyService.this, args);
				} finally {
					logger.trace("<-- End of " + method.getName() + "()");
				}
			}
		});
	}
	
	
	/**
	 * 
	 */
	protected boolean nodeExists(NodeRef nodeRef) {
		return nodeRepositoryService.exists(nodeRef);
	}
	
	/**
	 * Methode utilitaire pour savoir s'il y a eut un changement de certaines propriétés.
	 */
	protected boolean hasPropertiesChanged(Map<QName, Serializable> before, Map<QName, Serializable> after, PropertyModel<?> ... properties) {
		if (before == null || after == null) return true;
		
		for (PropertyModel<?> property : properties) {
			Serializable beforeValue = before.get(conversionService.getRequired(property.getNameReference()));
			Serializable  afterValue =  after.get(conversionService.getRequired(property.getNameReference()));
			if (! Objects.equals(beforeValue, afterValue)) {
				return true;
			}
		}
		return false;
	}
	protected <C extends Serializable> C getProperty(Map<QName, Serializable> value, SinglePropertyModel<C> property) {
		return conversionService.getProperty(value, property);
	}
	protected <C extends Serializable> List<C> getProperty(Map<QName, Serializable> value, MultiPropertyModel<C> property) {
		return conversionService.getProperty(value, property);
	}
	protected <E extends Enum<E>> E getProperty(Map<QName, Serializable> value, EnumTextPropertyModel<E> property) {
		return conversionService.getProperty(value, property);
	}	
	protected <C extends Serializable> List<C> getListNewItems(Map<QName, Serializable> before, Map<QName, Serializable> after, MultiPropertyModel<C> property) {
		List<C> beforeList = getProperty(before, property);
		List<C> afterList = getProperty(after, property);
		
		List<C> newItems = new ArrayList<>(afterList);
		newItems.removeAll(beforeList);
		return newItems;
	}
	protected <C extends Serializable> List<C> getListRemovedItems(Map<QName, Serializable> before, Map<QName, Serializable> after, MultiPropertyModel<C> property) {
		return getListNewItems(after, before, property);
	}
	
}
