package fr.openwide.alfresco.adf.maven.plugin;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

public abstract class AbstractAdfMojo extends AbstractMojo {

	@Parameter(readonly=true, defaultValue="${project}")
	protected MavenProject project;

	protected File getBaseDir() {
		return (project != null) ? project.getBasedir() : new File(".");
	}

}
