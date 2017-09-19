package fr.openwide.alfresco.repo.migrationtool.plugin;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Supprime tous les fichiers .ori du projet.
 * 
 * @author asauvez
 */
@Mojo(name="clean")
public class CleanMojo extends AbstractMigrationMojo {

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			parcoursResources("src/main/java/");
			parcoursResources("src/main/resources/");
		} catch (Exception e) {
			throw new MojoExecutionException(e.toString(), e);
		}
	}
	
	private void parcoursResources(String folderName) throws Exception {
		File folder = new File(getBaseDir(), folderName);
		parcoursResources(folder);
	}
	
	private void parcoursResources(File folder) throws Exception {
		if (folder.exists()) {
			for (File child : folder.listFiles()) {
				if (child.isDirectory()) {
					parcoursResources(child);
				} else {
					analyseResource(child);
				}
			}
		}
	}

	private void analyseResource(File file) throws Exception {
		if (file.getName().endsWith(originalFileExtension)) {
			getLog().debug("Delete " + file.getAbsolutePath());
			file.delete();
		}
	}
	
	public static void main(String[] args) throws MojoExecutionException, MojoFailureException {
		CleanMojo mojo = new CleanMojo();
		mojo.execute();
	}
}
