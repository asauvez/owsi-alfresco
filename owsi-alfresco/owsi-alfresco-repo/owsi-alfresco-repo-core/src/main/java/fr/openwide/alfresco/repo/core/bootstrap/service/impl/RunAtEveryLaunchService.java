package fr.openwide.alfresco.repo.core.bootstrap.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.extensions.surf.util.AbstractLifecycleBean;

import fr.openwide.alfresco.repo.core.bootstrap.service.RunAtEveryLaunchPatch;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateService;

@GenerateService(dependsOn = "patchExecuter")
public class RunAtEveryLaunchService extends AbstractLifecycleBean {

	private final Logger logger = LoggerFactory.getLogger(RunAtEveryLaunchService.class);
	
	@Autowired(required = false) private List<RunAtEveryLaunchPatch> patches;
	
	@Override
	protected void onBootstrap(ApplicationEvent event) {
		launchPatches();
	}
	
	public void launchPatches() {
		if (patches != null) {
			for (RunAtEveryLaunchPatch patch : patches) {
				logger.info("Run " + patch.getId());
				
				patch.applyAsync();
			}
		}
	}

	@Override
	protected void onShutdown(ApplicationEvent event) {}
}
