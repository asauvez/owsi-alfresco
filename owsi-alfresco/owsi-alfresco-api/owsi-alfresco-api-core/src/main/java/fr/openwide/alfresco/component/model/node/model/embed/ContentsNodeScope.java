package fr.openwide.alfresco.component.model.node.model.embed;

import java.io.File;
import java.io.OutputStream;

import fr.openwide.alfresco.api.core.node.binding.content.NodeContentDeserializer;
import fr.openwide.alfresco.api.core.node.binding.content.serializer.ByteArrayRepositoryContentSerializer;
import fr.openwide.alfresco.api.core.node.binding.content.serializer.FolderRepositoryContentSerializer;
import fr.openwide.alfresco.api.core.node.binding.content.serializer.JacksonRepositoryContentSerializer;
import fr.openwide.alfresco.api.core.node.binding.content.serializer.OutputStreamRepositoryContentDeserializer;
import fr.openwide.alfresco.api.core.node.binding.content.serializer.SingleFileRepositoryContentSerializer;
import fr.openwide.alfresco.api.core.node.binding.content.serializer.StringRepositoryContentSerializer;
import fr.openwide.alfresco.api.core.node.binding.content.serializer.TempFileRepositoryContentSerializer;
import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class ContentsNodeScope {

	private final NodeScopeBuilder builder;
	private final NodeScope scope;
	
	public ContentsNodeScope(NodeScopeBuilder builder) {
		this.builder = builder;
		this.scope = builder.getScope();
	}
	
	/** Indique que l'on souhaite downloader un fichier. Ici, sous forme de String */
	public NodeScopeBuilder asString() {
		return withDeserializer(StringRepositoryContentSerializer.INSTANCE);
	}
	public NodeScopeBuilder asByteArray() {
		return withDeserializer(ByteArrayRepositoryContentSerializer.INSTANCE);
	}
	public NodeScopeBuilder asTempFile() {
		return withDeserializer(TempFileRepositoryContentSerializer.INSTANCE);
	}
	public NodeScopeBuilder asSingleFile(File file) {
		return withDeserializer(new SingleFileRepositoryContentSerializer(file));
	}
	public NodeScopeBuilder asFilesInFolder(File destinationFolder) {
		builder.properties().name(); // on va avoir besoin du cm:name
		return withDeserializer(new FolderRepositoryContentSerializer(destinationFolder));
	}
	public NodeScopeBuilder asOutputStream(OutputStream outputStream) {
		return withDeserializer(new OutputStreamRepositoryContentDeserializer(outputStream));
	}
	public <T> NodeScopeBuilder asJackson(Class<T> clazz) {
		return withDeserializer(new JacksonRepositoryContentSerializer<T>(clazz));
	}

	
	/** Indique que l'on souhaite uploader un fichier. Le format sera pris en fonction du type d'objet. */
	public NodeScopeBuilder forUpload() {
		return asTempFile();
	}
	
	public NodeScopeBuilder withDeserializer(NodeContentDeserializer<?> deserializer) {
		return withDeserializer(CmModel.content.content, deserializer);
	}
	public NodeScopeBuilder withDeserializer(PropertyModel<?> propertyModel, NodeContentDeserializer<?> deserializer) {
		scope.getProperties().add(propertyModel.getNameReference());
		scope.getContentDeserializers().put(propertyModel.getNameReference(), deserializer);
		return builder;
	}
	
}
