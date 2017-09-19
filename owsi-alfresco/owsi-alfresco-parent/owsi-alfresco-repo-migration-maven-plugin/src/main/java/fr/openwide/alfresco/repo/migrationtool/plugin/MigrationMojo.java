package fr.openwide.alfresco.repo.migrationtool.plugin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Plugin Maven d'aide à la migration des patch Alfresco.
 * 
 * Pour chaque fichier patché de Alfresco, créer un fichier .x.y.z.ori avec le fichier original extrait des JAR Alfresco.
 * Par exemple, pour un fichier contentModel.xml surchargé, le plugin crée un fichier contentModel.xml.5.2.1.ori.
 * 
 * Le plugin est automatiquement appelé si le pom.xml hérite de owsi-alfresco-parent-repo-component, 
 * owsi-alfresco-parent-alfresco ou owsi-alfresco-parent-share.
 * 
 * Il est préférable d'exclure les fichiers *.ori des fichiers JAR ou WAR générés. C'est fait automatiquement si le
 * pom.xml hérite des mêmes modules.
 * 
 * @author asauvez
 */
@Mojo(name="migration", defaultPhase=LifecyclePhase.PACKAGE)
public class MigrationMojo extends AbstractMigrationMojo {

	private static final String WEB_INF_CLASSES = "WEB-INF/classes";

	@Parameter(defaultValue="true")
	private boolean createMissingFile = true;

	@Parameter(property="alfresco.version", defaultValue="true")
	private String alfrescoVersion;

	private Map<String, File> resourceInJarByPath = new HashMap<String, File>();
	private Map<String, File> resourceInWarByPath = new HashMap<String, File>();
	
	private StringBuilder errors = new StringBuilder();
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			initAlfrescoJars();
			
			// parcours src/main/java/org/alfresco/
			visitResources(new File(getBaseDir(), "src/main/resources/alfresco"), "/alfresco");
		} catch (Exception e) {
			throw new MojoExecutionException(e.toString(), e);
		}
		if (errors.length() > 0) {
			throw new MojoFailureException(errors.toString().substring(0, errors.length()-1));
		}
	}
	
	private void error(String msg) {
		getLog().error(msg);
		errors.append(msg).append("\n");
	}
	
	private void initAlfrescoJars() throws Exception {
		if (project != null && alfrescoVersion == null) {
			alfrescoVersion = project.getProperties().getProperty("owsi-alfresco.repo.alfresco.version");
			if (alfrescoVersion == null) {
				alfrescoVersion = project.getProperties().getProperty("alfresco.version");
			}
		}
		
		File userHome = new File(System.getProperty("user.home"));
		File m2Repository = new File(userHome, ".m2/repository/");
		for (File alfrescoRepo : new File[] {
				new File(m2Repository, "org/alfresco/"),
				new File(m2Repository, "org/alfresco-entreprise/")
		}) {
			for (File module : alfrescoRepo.listFiles()) {
				File version = new File(module, alfrescoVersion);
				if (version.exists()) {
					File jar = new File(version, module.getName() + "-" + version.getName() + ".jar");
					if (jar.exists()) {
						ZipInputStream zip = new ZipInputStream(new BufferedInputStream(new FileInputStream(jar)));
						try {
							ZipEntry entry;
							while ((entry = zip.getNextEntry()) != null) {
								if (! entry.getName().endsWith(".class")) {
									resourceInJarByPath.put("/" + entry.getName(), jar);
								}
							}
						} finally {
							zip.close();
						}
					}
					File jarClasses = new File(version, module.getName() + "-" + version.getName() + "-classes.jar");
					if (jarClasses.exists()) {
						ZipInputStream zip = new ZipInputStream(new BufferedInputStream(new FileInputStream(jarClasses)));
						try {
							ZipEntry entry;
							while ((entry = zip.getNextEntry()) != null) {
								if (! entry.getName().endsWith(".class")) {
									resourceInJarByPath.put("/" + entry.getName(), jarClasses);
								}
							}
						} finally {
							zip.close();
						}
					}
					
					File war = new File(version, module.getName() + "-" + version.getName() + ".war");
					if (war.exists()) {
						ZipInputStream zip = new ZipInputStream(new BufferedInputStream(new FileInputStream(war)));
						try {
							ZipEntry entry;
							while ((entry = zip.getNextEntry()) != null) {
								if (entry.getName().startsWith(WEB_INF_CLASSES) && ! entry.getName().endsWith(".class")) {
									resourceInWarByPath.put(entry.getName().substring(WEB_INF_CLASSES.length()), war);
								}
							}
						} finally {
							zip.close();
						}
					}
				}
			}
		}
	}
	
	private void visitResources(File folder, String path) throws Exception {
		if (folder.exists()) {
			for (File child : folder.listFiles()) {
				String childPath = path + "/" + child.getName();
				if (child.isDirectory()) {
					visitResources(child, childPath);
				} else {
					analyzeResource(child, childPath);
				}
			}
		}
	}

	private void analyzeResource(File file, String path) throws Exception {
		String versionExtension = "." + alfrescoVersion + originalFileExtension;
		if (path.endsWith(originalFileExtension)) {
			if (path.endsWith(ignoreFileExtension)) {
				File patch = new File(file.getParentFile(), file.getName().substring(0, file.getName().length() - ignoreFileExtension.length()));
				if (! patch.exists()) {
					error(".ignore.ori file without corresponding patch file: " + file.getAbsolutePath());
				}
			} else if (path.endsWith(versionExtension)) {
				File patch = new File(file.getParentFile(), file.getName().substring(0, file.getName().length() - versionExtension.length()));
				if (! patch.exists()) {
					error("Original file without corresponding patch file: " + file.getAbsolutePath());
				}
			} else {
				error("You should migrate this original file to " + alfrescoVersion + " then delete it: " + file.getAbsolutePath());
			}
			return;
		}
		
		getLog().debug("Analyse " + path);
		String originalContent = findContentByPath(path);
		
		boolean customizedResource = 
				   (path.startsWith("/alfresco/extension/templates/webscripts/") && ! path.startsWith("/alfresco/extension/templates/webscripts/org/alfresco/"))
				|| path.startsWith("/alfresco/web-extension/");
		if (customizedResource) {
			if (originalContent != null) {
				error("Customization path shoud contains patched file : " + file.getAbsolutePath());
			}
			return;
		}
		
		if (originalContent == null) {
			File ignore = new File(file.getParentFile(), file.getName() + ignoreFileExtension);
			if (ignore.exists()) {
				getLog().warn("Ignore resource " + file.getAbsolutePath());
				return;
			} else {
				error("Original resource not found for " + file.getAbsolutePath());
				return;
			}
		}
		File original = new File(file.getParentFile(), file.getName() + versionExtension);
		if (original.exists()) {
			String copyContent = FileUtils.readFileToString(original);
			if (! originalContent.equals(copyContent)) {
				error("Copy file different from original " + original.getAbsolutePath());
			}
			
			String patchedContent = FileUtils.readFileToString(file);
			if (originalContent.equals(patchedContent)) {
				error("unecessary patch file. Identical resource:" + file.getAbsolutePath());
			}
		} else {
			if (createMissingFile) {
				getLog().warn("Create missing file " + original.getAbsolutePath());
				FileUtils.write(original, originalContent);
			} else {
				error("Copy file not found and createMissingFile=false: " + original.getAbsolutePath());
			}
		}
	}
	
	private String findContentByPath(String path) throws Exception {
		if (path.startsWith("/alfresco/extension/templates/")) {
			path = path.replace("/alfresco/extension/templates/", "/alfresco/templates/");
		}
		
		File war = resourceInWarByPath.get(path);
		if (war != null) {
			ZipInputStream zip = new ZipInputStream(new BufferedInputStream(new FileInputStream(war)));
			try {
				ZipEntry entry;
				while ((entry = zip.getNextEntry()) != null) {
					if (entry.getName().equals(WEB_INF_CLASSES + path)) {
						return IOUtils.toString(zip);
					}
				}
			} finally {
				zip.close();
			}
			throw new IllegalStateException(path + " - " + war.getAbsolutePath());
		}
		
		File jar = resourceInJarByPath.get(path);
		if (jar != null) {
			ZipInputStream zip = new ZipInputStream(new BufferedInputStream(new FileInputStream(jar)));
			try {
				ZipEntry entry;
				while ((entry = zip.getNextEntry()) != null) {
					if (entry.getName().equals(path.substring("/".length()))) {
						return IOUtils.toString(zip);
					}
				}
			} finally {
				zip.close();
			}
			throw new IllegalStateException(path + " - " + jar.getAbsolutePath());
		}
		return null;
	}
	
	public static void main(String[] args) throws MojoExecutionException, MojoFailureException {
		MigrationMojo mojo = new MigrationMojo();
		mojo.alfrescoVersion = "5.2.1";
		mojo.execute();
	}
}
