package fr.openwide.alfresco.repo.dictionary.workflow.service.impl;

import java.io.Serializable;
import java.util.List;

import org.activiti.engine.delegate.VariableScope;
import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.bean.NodeBean;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.EnumTextPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.SinglePropertyModel;
import fr.openwide.alfresco.repo.dictionary.workflow.service.WorkflowContextService;

public class WorkflowContextServiceImpl implements WorkflowContextService {

	@Override
	@SuppressWarnings("unchecked")
	public <C extends Serializable> C getVariable(VariableScope context, QName property) {
		String workflowName = property.getPrefixString() + "_" + property.getLocalName();
		return (C) context.getVariable(workflowName);
	}
	@Override
	public <C extends Serializable> C getVariable(VariableScope context, SinglePropertyModel<C> property) {
		return getVariable(context, property.getQName());
	}
	@Override
	public <E extends Enum<E>> E getVariable(VariableScope context, EnumTextPropertyModel<E> property) {
		return NodeBean.textToEnum(property, getVariable(context, property.getQName()));
	}
	@Override
	public <C extends Serializable> List<C> getVariable(VariableScope context, MultiPropertyModel<C> property) {
		return getVariable(context, property.getQName());
	}
	
	@Override
	public void setVariable(VariableScope context, QName property, Serializable value) {
		String workflowName = property.getPrefixString() + "_" + property.getLocalName();
		context.setVariable(workflowName, value);
	}
	@Override
	public <C extends Serializable> void setVariable(VariableScope context, SinglePropertyModel<C> property, C value) {
		setVariable(context, property.getQName(), value);
	}
	@Override
	public <E extends Enum<E>> void setVariable(VariableScope context, EnumTextPropertyModel<E> property, E value) {
		String code = NodeBean.enumToText(value);
		setVariable(context, property.getQName(), code);
	}
	@Override
	public <C extends Serializable> void setVariable(VariableScope context, MultiPropertyModel<C> property, List<C> value) {
		setVariable(context, property.getQName(), (Serializable) value);
	}
}
