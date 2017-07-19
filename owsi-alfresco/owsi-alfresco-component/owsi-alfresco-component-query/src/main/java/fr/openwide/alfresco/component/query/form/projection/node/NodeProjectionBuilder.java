package fr.openwide.alfresco.component.query.form.projection.node;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.SinglePropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.component.query.form.projection.Projection;
import fr.openwide.alfresco.component.query.form.projection.Projection.Align;
import fr.openwide.alfresco.component.query.form.projection.ProjectionBuilder;
import fr.openwide.alfresco.component.query.form.view.output.IconOutputFieldView;
import fr.openwide.alfresco.component.query.form.view.output.OutputFieldView;
import fr.openwide.alfresco.api.core.node.model.RepositoryContentData;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.node.model.PermissionReference;
import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;

public class NodeProjectionBuilder extends ProjectionBuilder<RepositoryNode, NodeProjectionBuilder> {

	public Projection<RepositoryNode, NodeProjectionBuilder, NodeReference> ref() {
		return add(new NodeReferenceProjectionImpl(this));
	}

	public Projection<RepositoryNode, NodeProjectionBuilder, String> path() {
		return add(new NodePathProjectionImpl(this));
	}

	public Projection<RepositoryNode, NodeProjectionBuilder, NameReference> type() {
		return add(new NodeTypeProjectionImpl(this));
	}

	public <P extends Serializable> Projection<RepositoryNode, NodeProjectionBuilder, P> prop(SinglePropertyModel<P> property) {
		return add(new NodePropertyProjectionImpl<P>(this, property));
	}
	public <P extends Serializable> Projection<RepositoryNode, NodeProjectionBuilder, List<P>> prop(MultiPropertyModel<P> property) {
		return add(new NodeMultiPropertyProjectionImpl<P>(this, property));
	}

	public Projection<RepositoryNode, NodeProjectionBuilder, RepositoryNode> node() {
		return add(new NodeProjectionImpl(this))
			.asCustom();
	}
	
	public Projection<RepositoryNode, NodeProjectionBuilder, RepositoryNode> icon() {
		type().visible(false).of();
		contentMimeType().visible(false).of();
		
		return add(new NodeProjectionImpl(this)
			.transform(new Function<RepositoryNode, Object>() {
					@Override
					public Object apply(RepositoryNode node) {
						if (CmModel.folder.getNameReference().equals(node.getType())) {
							return new IconOutputFieldView("glyphicon glyphicon-folder-close", "mimetype.folder");
						} else if (CmModel.content.getNameReference().equals(node.getType())) {
							RepositoryContentData content = node.getProperty(CmModel.content.content.getNameReference(), RepositoryContentData.class);
							if (content.getMimetype().startsWith("text/")
								|| content.getMimetype().equals("application/vnd.oasis.opendocument.text")
								|| content.getMimetype().equals("application/msword")
								|| content.getMimetype().equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
								return new IconOutputFieldView("glyphicon glyphicon-font", "mimetype.text");
							} else if (content.getMimetype().equals("application/vnd.ms-excel") 
									|| content.getMimetype().equals("application/vnd.oasis.opendocument.spreadsheet")
									|| content.getMimetype().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
								return new IconOutputFieldView("glyphicon glyphicon-stats", "mimetype.spreadsheet");
							} else if (content.getMimetype().equals("application/vnd.ms-powerpoint") 
									|| content.getMimetype().equals("application/vnd.oasis.opendocument.presentation")) {
								return new IconOutputFieldView("glyphicon glyphicon-facetime-video", "mimetype.presentation");
							} else if (content.getMimetype().equals("application/pdf")) {
								return new IconOutputFieldView("glyphicon glyphicon-print", "mimetype.pdf");
							} else if (content.getMimetype().startsWith("image/")) {
								return new IconOutputFieldView("glyphicon glyphicon-picture", "mimetype.image");
							} else if (content.getMimetype().startsWith("audio/")) {
								return new IconOutputFieldView("glyphicon glyphicon-music", "mimetype.audio");
							} else if (content.getMimetype().startsWith("video/")) {
								return new IconOutputFieldView("glyphicon glyphicon-volume-film", "mimetype.video");
							} else if (content.getMimetype().equals("application/zip")) {
								return new IconOutputFieldView("glyphicon glyphicon-compressed", "mimetype.zip");
							} 
							return new IconOutputFieldView("glyphicon glyphicon-file", "mimetype.file");
						} else if (CmModel.person.getNameReference().equals(node.getType())) {
							return new IconOutputFieldView("glyphicon glyphicon-use", "mimetype.person");
						}
						return null;
					}
				})
			.setView(OutputFieldView.ICON)
			.align(Align.CENTER));
	}
	
	public Projection<RepositoryNode, NodeProjectionBuilder, RepositoryContentData> contentSize() {
		return contentSize(CmModel.content.content);
	}
	public Projection<RepositoryNode, NodeProjectionBuilder, RepositoryContentData> contentSize(SinglePropertyModel<RepositoryContentData> property) {
		return prop(property)
			.label("contentSize")
			.align(Align.RIGHT)
			.transform(new Function<RepositoryContentData, Object>() {
				@Override
				public String apply(RepositoryContentData content) {
					return (content != null) ? FileUtils.byteCountToDisplaySize(content.getSize()) : null;
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
	public Projection<RepositoryNode, NodeProjectionBuilder, RepositoryContentData> contentMimeType(SinglePropertyModel<RepositoryContentData> property) {
		return prop(property)
			.label("contentMimeType")
			.transform(new Function<RepositoryContentData, Object>() {
				@Override
				public String apply(RepositoryContentData content) {
					return (content != null) ? content.getMimetypeDisplay() : null;
				}
			})
			.comparator(new Function<RepositoryContentData, String>() {
				@Override
				public String apply(RepositoryContentData content) {
					return content.getMimetypeDisplay();
				}
			});
	}

	public Projection<RepositoryNode, NodeProjectionBuilder, String> contentString() {
		return contentString(CmModel.content.content);
	}
	public Projection<RepositoryNode, NodeProjectionBuilder, String> contentString(PropertyModel<RepositoryContentData> property) {
		return add(new NodeContentStringProjectionImpl(this, property)); 
	}

	public Predicate<RepositoryNode> ifHasPermission(PermissionReference permission) {
		return new UserPermissionPredicate(permission);
	}

}
