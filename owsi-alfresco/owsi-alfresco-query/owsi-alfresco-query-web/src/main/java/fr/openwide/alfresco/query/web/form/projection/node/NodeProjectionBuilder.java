package fr.openwide.alfresco.query.web.form.projection.node;

import java.io.Serializable;

import org.apache.commons.io.FileUtils;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

import fr.openwide.alfresco.query.core.node.model.property.PropertyModel;
import fr.openwide.alfresco.query.core.repository.model.CmModel;
import fr.openwide.alfresco.query.web.form.projection.Projection;
import fr.openwide.alfresco.query.web.form.projection.Projection.Align;
import fr.openwide.alfresco.query.web.form.projection.ProjectionBuilder;
import fr.openwide.alfresco.repository.api.node.model.RepositoryContentData;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.node.model.RepositoryPermission;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

public class NodeProjectionBuilder extends ProjectionBuilder<RepositoryNode, NodeProjectionBuilder> {

	public Projection<RepositoryNode, NodeProjectionBuilder, NodeReference> ref() {
		return add(new NodeReferenceProjectionImpl(this));
	}

	public Projection<RepositoryNode, NodeProjectionBuilder, NameReference> type() {
		return add(new NodeTypeProjectionImpl(this));
	}

	public <P extends Serializable> Projection<RepositoryNode, NodeProjectionBuilder, P> prop(PropertyModel<P> property) {
		return add(new NodePropertyProjectionImpl<P>(this, property));
	}

	public Projection<RepositoryNode, NodeProjectionBuilder, RepositoryContentData> contentSize() {
		return contentSize(CmModel.content.content);
	}
	public Projection<RepositoryNode, NodeProjectionBuilder, RepositoryContentData> contentSize(PropertyModel<RepositoryContentData> property) {
		return prop(property)
			.label("contentSize")
			.align(Align.RIGHT)
			.transform(new Function<RepositoryContentData, Object>() {
				@Override
				public String apply(RepositoryContentData content) {
					return FileUtils.byteCountToDisplaySize(content.getSize());
				}
			})
			.comparator(new Function<RepositoryContentData, Long>() {
				@Override
				public Long apply(RepositoryContentData content) {
					return content.getSize();
				}
			});
	}

	public Projection<RepositoryNode, NodeProjectionBuilder, RepositoryContentData> contentMimeType() {
		return contentMimeType(CmModel.content.content);
	}
	public Projection<RepositoryNode, NodeProjectionBuilder, RepositoryContentData> contentMimeType(PropertyModel<RepositoryContentData> property) {
		return prop(property)
			.label("contentMimeType")
			.transform(new Function<RepositoryContentData, Object>() {
				@Override
				public String apply(RepositoryContentData content) {
					return content.getMimetypeDisplay();
				}
			})
			.comparator(new Function<RepositoryContentData, String>() {
				@Override
				public String apply(RepositoryContentData content) {
					return content.getMimetypeDisplay();
				}
			});
	}
	
	public Predicate<RepositoryNode> ifHasPermission(RepositoryPermission permission) {
		return new UserPermissionPredicate(permission);
	}

}
