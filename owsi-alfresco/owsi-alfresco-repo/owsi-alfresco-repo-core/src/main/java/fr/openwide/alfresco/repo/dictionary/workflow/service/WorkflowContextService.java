package fr.openwide.alfresco.repo.dictionary.workflow.service;

import java.io.Serializable;
import java.util.List;

import org.activiti.engine.delegate.VariableScope;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.EnumTextPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.SinglePropertyModel;

public interface WorkflowContextService {

	<C extends Serializable> C getVariable(VariableScope context, NameReference property);
	<C extends Serializable> C getVariable(VariableScope context, SinglePropertyModel<C> property);
	<E extends Enum<E>> E getVariable(VariableScope context, EnumTextPropertyModel<E> property);
	<C extends Serializable> List<C> getVariable(VariableScope context, MultiPropertyModel<C> property);

	void setVariable(VariableScope context, NameReference property, Serializable value);
	<C extends Serializable> void setVariable(VariableScope context, SinglePropertyModel<C> property, C value);
	<E extends Enum<E>> void setVariable(VariableScope context, EnumTextPropertyModel<E> property, E value);
	<C extends Serializable> void setVariable(VariableScope context, MultiPropertyModel<C> property, List<C> value);

}
