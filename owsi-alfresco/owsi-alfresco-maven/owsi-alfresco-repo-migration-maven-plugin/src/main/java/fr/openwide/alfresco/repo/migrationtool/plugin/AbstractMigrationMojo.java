package fr.openwide.alfresco.repo.migrationtool.plugin;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

public abstract class AbstractMigrationMojo extends AbstractMojo {

	@Parameter(readonly=true, defaultValue="${project}")
	protected MavenProject project;

	@Parameter(defaultValue=".ori")
	protected String originalFileExtension = ".ori";

	@Parameter(defaultValue=".ignore.ori")
	protected String ignoreFileExtension = ".ignore.ori";

	@Parameter(defaultValue="--")
	protected String versionSeparator = "--";

	protected File getBaseDir() {
		return (project != null) ? project.getBasedir() : new File(".");
	}

}
