package fr.openwide.alfresco.repo.module.classification.model.policy;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.repo.module.classification.model.ClassificationEvent;
import fr.openwide.alfresco.repo.module.classification.model.builder.ClassificationBuilder;

public class ClassificationPolicies {

	public static ClassificationPolicy<ContainerModel> yyyyMM(String projectName) {
		return new ClassificationPolicy<ContainerModel>() {
			@Override public void initNodeScopeBuilder(NodeScopeBuilder nodeScopeBuilder) {}
			@Override
			public void classify(ClassificationBuilder builder, ContainerModel model, ClassificationEvent event) {
				builder.rootCompanyHome()
					.subFolder(projectName)
					.subFolderYear()
					.subFolderMonth()
					.moveNode();
			}
		};
	}
	
	public static ClassificationPolicy<ContainerModel> yyyyMMdd(String projectName) {
		return new ClassificationPolicy<ContainerModel>() {
			@Override public void initNodeScopeBuilder(NodeScopeBuilder nodeScopeBuilder) {}
			@Override
			public void classify(ClassificationBuilder builder, ContainerModel model, ClassificationEvent event) {
				builder.rootCompanyHome()
					.subFolder(projectName)
					.subFolderYear()
					.subFolderMonth()
					.subFolderDay()
					.moveNode();
			}
		};
	}
	
	private ClassificationPolicies() {}

}
