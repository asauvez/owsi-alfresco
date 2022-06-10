package fr.openwide.alfresco.repo.module.classification.model.builder;

import java.util.Optional;
import java.util.function.Consumer;

import org.alfresco.service.cmr.repository.NodeRef;

import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.SinglePropertyModel;

public class PreviousVersionBuilder<B extends AbstractClassificationBuilder<B>> {

	private B classificationBuilder;
	private Optional<NodeRef> previousNodeRefIfPresent;
	
	public PreviousVersionBuilder(B classificationBuilder, SinglePropertyModel<?>[] properties) {
		this.classificationBuilder = classificationBuilder;
		previousNodeRefIfPresent = classificationBuilder.service.getPreviousWith(classificationBuilder, properties);
	}
	
	public Optional<NodeRef> getPreviousNodeRef() {
		return previousNodeRefIfPresent;
	}
	
	public B ifPresent(Consumer<NodeRef> consumer) {
		previousNodeRefIfPresent.ifPresent(consumer);
		return classificationBuilder;
	}
	
	
	public B delete() {
		return ifPresent(previousNodeRef -> {
			classificationBuilder.service.delete(previousNodeRef, false);
		}); 
	}

	public B error() {
		return ifPresent(previousNodeRef -> {
			throw new IllegalStateException("There is already a node with the same properties.");
		}); 
	}

	public B newVersion(PropertyModel<?> ... propertiesToCopy) {
		return ifPresent(previousNodeRef -> {
			classificationBuilder.service.newContentVersion(classificationBuilder.getNodeRef(), previousNodeRef, propertiesToCopy);
			classificationBuilder.getNodeModelService().deleteNode(classificationBuilder.getNodeRef());
			classificationBuilder.getEvent().setNodeRef(previousNodeRef);
		}); 
	}
}
