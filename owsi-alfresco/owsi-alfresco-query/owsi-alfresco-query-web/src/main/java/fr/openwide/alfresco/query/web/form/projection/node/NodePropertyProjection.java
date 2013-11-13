package fr.openwide.alfresco.query.web.form.projection.node;

import fr.openwide.alfresco.query.core.node.model.property.PropertyModel;
import fr.openwide.alfresco.query.core.node.model.value.NameReference;
import fr.openwide.alfresco.query.core.search.model.NodeFetchDetails;
import fr.openwide.alfresco.query.core.search.model.NodeResult;
import fr.openwide.alfresco.query.web.form.projection.Projection;
import fr.openwide.alfresco.query.web.form.projection.ProjectionBuilder;

public class NodePropertyProjection<P> extends Projection<P> {

	private final PropertyModel<?> property;

	public NodePropertyProjection(ProjectionBuilder builder, PropertyModel<P> property) {
		super(builder, property.getValueClass());
		this.property = property;		
	}

	@Override
	public String getDefaultLabelCode() {
		NameReference nameReference = property.getNameReference();
		return nameReference.getNamespace() + "_" + nameReference.getName();
	}

	@Override
	@SuppressWarnings("unchecked")
	public P apply(NodeResult result) {
		return (P) result.get(property);
	}

	@Override
	public void initNodeFetchDetails(NodeFetchDetails nodeFetchDetails) {
		super.initNodeFetchDetails(nodeFetchDetails);
		
		nodeFetchDetails.getProperties().add(property.getNameReference());
	}
}
