package fr.openwide.alfresco.repo.core.bootstrap.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.extensions.surf.util.AbstractLifecycleBean;

import fr.openwide.alfresco.repo.core.bootstrap.service.RunAtEveryLaunchPatch;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateService;

@GenerateService(dependsOn = "patchExecuter")
public class RunAtEveryLaunchService extends AbstractLifecycleBean {

	@Autowired private List<RunAtEveryLaunchPatch> patches;
	
	@Override
	protected void onBootstrap(ApplicationEvent event) {
		for (RunAtEveryLaunchPatch patch : patches) {
			patch.applyAsync();
		}
	}

	@Override
	protected void onShutdown(ApplicationEvent event) {}
}
