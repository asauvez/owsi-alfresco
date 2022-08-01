package fr.openwide.alfresco.component.model.node.model.property.single;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;

public class NodeRefPropertyModel extends SinglePropertyModel<NodeRef> {

	public NodeRefPropertyModel(ContainerModel type, QName qName) {
		super(type, qName);
	}

	@Override
	public Class<NodeRef> getValueClass() {
		return NodeRef.class;
	}

	@Override
	public String getDataType() {
		return "d:noderef";
	}
}
