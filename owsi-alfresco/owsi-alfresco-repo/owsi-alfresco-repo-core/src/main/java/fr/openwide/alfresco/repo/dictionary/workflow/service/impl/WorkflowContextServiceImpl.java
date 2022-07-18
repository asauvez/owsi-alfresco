package fr.openwide.alfresco.repo.dictionary.workflow.service.impl;

import java.io.Serializable;
import java.util.List;

import org.activiti.engine.delegate.VariableScope;
import org.springframework.beans.factory.annotation.Autowired;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.bean.NodeBean;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.EnumTextPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.SinglePropertyModel;
import fr.openwide.alfresco.repo.dictionary.workflow.service.WorkflowContextService;
import fr.openwide.alfresco.repo.remote.conversion.service.ConversionService;

public class WorkflowContextServiceImpl implements WorkflowContextService {

	@Autowired
	private ConversionService conversionService;
	
	@Override
	@SuppressWarnings("unchecked")
	public <C extends Serializable> C getVariable(VariableScope context, NameReference property) {
		return (C) conversionService.getForApplication((Serializable) context.getVariable(property.getWorkflowName()));
	}
	@Override
	public <C extends Serializable> C getVariable(VariableScope context, SinglePropertyModel<C> property) {
		return getVariable(context, property.getNameReference());
	}
	@Override
	public <E extends Enum<E>> E getVariable(VariableScope context, EnumTextPropertyModel<E> property) {
		return NodeBean.textToEnum(property, getVariable(context, property.getNameReference()));
	}
	@Override
	public <C extends Serializable> List<C> getVariable(VariableScope context, MultiPropertyModel<C> property) {
		return getVariable(context, property.getNameReference());
	}
	
	@Override
	public void setVariable(VariableScope context, NameReference property, Serializable value) {
		context.setVariable(property.getWorkflowName(), conversionService.getForRepository(value));
	}
	@Override
	public <C extends Serializable> void setVariable(VariableScope context, SinglePropertyModel<C> property, C value) {
		setVariable(context, property.getNameReference(), value);
	}
	@Override
	public <E extends Enum<E>> void setVariable(VariableScope context, EnumTextPropertyModel<E> property, E value) {
		String code = NodeBean.enumToText(value);
		setVariable(context, property.getNameReference(), code);
	}
	@Override
	public <C extends Serializable> void setVariable(VariableScope context, MultiPropertyModel<C> property, List<C> value) {
		setVariable(context, property.getNameReference(), (Serializable) value);
	}
}
