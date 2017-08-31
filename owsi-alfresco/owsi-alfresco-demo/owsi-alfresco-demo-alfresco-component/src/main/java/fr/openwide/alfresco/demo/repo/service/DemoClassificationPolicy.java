package fr.openwide.alfresco.demo.repo.service;

import fr.openwide.alfresco.api.core.authority.model.AuthorityReference;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.demo.business.model.DemoModel;
import fr.openwide.alfresco.demo.business.model.demo.DemoAspect;
import fr.openwide.alfresco.repo.module.classification.model.ClassificationBuilder;
import fr.openwide.alfresco.repo.module.classification.model.ClassificationEvent;
import fr.openwide.alfresco.repo.module.classification.model.ClassificationPolicy;

public class DemoClassificationPolicy implements ClassificationPolicy<DemoAspect> {

	@Override
	public void initNodeScopeBuilder(NodeScopeBuilder nodeScopeBuilder) {}

	@Override
	public void classify(ClassificationBuilder builder, DemoAspect model, ClassificationEvent event) {
		if ("unique".equals(event.getNode().properties().get(model.demoProperty))) {
			builder
				.rootFolderIdentifier(DemoModel.DEMO_ROOT_FOLDER)
				.subFolder("classificationUnique")
				.subFolder("toto", node -> node
					.aspect(CmModel.emailed)
					.permissions().addAccessControlConsumer(AuthorityReference.GROUP_EVERYONE))
				.uniqueName()
				.moveNode();
			return;
		}
		
		builder
			.rootFolderIdentifier(DemoModel.DEMO_ROOT_FOLDER)
			.subFolder("classification")
			.subFolderProperty(model.demoProperty)
			.subFolderProperty(CmModel.auditable.creator)
			.subFolderYear()
			.subFolderMonth()
			.moveNode();
		
		builder
			.unlinkSecondaryParents()
			.rootFolderIdentifier(DemoModel.DEMO_ROOT_FOLDER)
			.subFolder("classification2")
			.createSecondaryParent();
		
		builder
			.rootFolderIdentifier(DemoModel.DEMO_ROOT_FOLDER)
			.subFolder("classification3")
			.deletePrevious()
			.copyNode();

		builder
			.unlinkSecondaryParents()
			.rootFolderIdentifier(DemoModel.DEMO_ROOT_FOLDER)
			.subFolder("classification4")
			.createFileLink();
	}

}