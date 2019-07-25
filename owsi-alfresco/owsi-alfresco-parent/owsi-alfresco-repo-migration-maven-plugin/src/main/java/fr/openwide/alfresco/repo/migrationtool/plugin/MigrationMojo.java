package fr.openwide.alfresco.repo.migrationtool.plugin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import fr.openwide.alfresco.repo.migrationtool.plugin.model.Customization;
import fr.openwide.alfresco.repo.migrationtool.plugin.model.Extension;
import fr.openwide.alfresco.repo.migrationtool.plugin.model.Module;

/**
 * Plugin Maven d'aide à la migration des patch Alfresco.
 * 
 * Pour chaque fichier patché de Alfresco, créer un fichier .x.y.z.ori avec le fichier original extrait des JAR Alfresco.
 * Par exemple, pour un fichier contentModel.xml surchargé, le plugin crée un fichier contentModel.xml.6.0.0.ori.
 * 
 * Le plugin est automatiquement appelé si le pom.xml hérite de owsi-alfresco-parent-repo-component, 
 * owsi-alfresco-parent-alfresco ou owsi-alfresco-parent-share.
 * 
 * Il est préférable d'exclure les fichiers *.ori des fichiers JAR ou WAR générés. C'est fait automatiquement si le
 * pom.xml hérite des mêmes modules.
 * 
 * @author asauvez
 */
@Mojo(name="migration", defaultPhase=LifecyclePhase.COMPILE)
public class MigrationMojo extends AbstractMigrationMojo {

	private static final String WEB_INF_CLASSES = "WEB-INF/classes";

	@Parameter(defaultValue="true")
	private boolean enabled = true;

	@Parameter(defaultValue="true")
	private boolean createMissingFile = true;
	
	@Parameter(defaultValue="false")
	private boolean createMissingIgnoreFile = false;
	
	@Parameter(defaultValue="true")
	private boolean deleteIdenticalResources = true;

	@Parameter(property="alfresco.version", defaultValue="6.0.0")
	private String alfrescoVersion;

	private Map<String, File> resourceInJarByPath = new HashMap<String, File>();
	private Map<String, File> resourceInWarByPath = new HashMap<String, File>();
	private Set<String> customizationFoldersToIgnore = new HashSet<String>();
	
	private StringBuilder errors = new StringBuilder();
	
	private Unmarshaller unmarshaller = JAXBContext.newInstance(Extension.class).createUnmarshaller();
	
	public MigrationMojo() throws Exception {}
	
	private class MigrationStat {
		public int nbIgnored = 0;
		public int nbPatch = 0;
		public int nbModule = 0;
		@Override
		public String toString() {
			return "nbIgnored=" + nbIgnored + " nbPatch=" + nbPatch + " nbModule=" + nbModule;
		}
	}
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (! enabled) {
			getLog().warn("Migration tools disabled.");
			return;
		}
		try {
			initDependencyJars();
			
			// parcours src/main/java/org/alfresco/
			MigrationStat stat = new MigrationStat();
			visitResources(new File(getBaseDir(), "src/main/resources/alfresco/web-extension/site-data/extensions/"), "/alfresco/web-extension/site-data/extensions/", stat);
			customizationFoldersToIgnore.add("/alfresco/web-extension");
			
			visitResources(new File(getBaseDir(), "src/main/resources/alfresco"), "/alfresco", stat);
			visitResources(new File(getBaseDir(), "src/main/webapp"), "", stat);
			visitResources(new File(getBaseDir(), "src/main/java/org/alfresco"), "/org/alfresco", stat);
			getLog().info("Main stat " + stat.toString());
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
	
	private void initDependencyJars() throws Exception {
		@SuppressWarnings("unchecked")
		Set<Artifact> dependencyArtifacts = project.getDependencyArtifacts();
		
		getLog().debug("Artifacts : " + project.getDependencyArtifacts());
		
		for (Artifact artifact : dependencyArtifacts) {
			File file = artifact.getFile();

			if (file.exists()) {
				ZipInputStream zip = new ZipInputStream(new BufferedInputStream(new FileInputStream(file)));
				try {
					ZipEntry entry;
					while ((entry = zip.getNextEntry()) != null) {
						if (! entry.isDirectory() && ! entry.getName().endsWith(".class")) {
							if (entry.getName().startsWith(WEB_INF_CLASSES)) {
								put(resourceInWarByPath, entry.getName().substring(WEB_INF_CLASSES.length()), file);
							} else if (entry.getName().startsWith("WEB-INF/lib")) {
								// Ignore librairie externe pour le moment
							} else {
								put(resourceInJarByPath, "/" + entry.getName(), file);
							}
						}
					}
				} finally {
					zip.close();
				}
			}
			
//			File source = new File(file.getParentFile(), file.getName().substring(0, file.getName().lastIndexOf('.')) + "-sources.jar");
//			if (source.exists()) {
//				ZipInputStream zip = new ZipInputStream(new BufferedInputStream(new FileInputStream(source)));
//				try {
//					ZipEntry entry;
//					while ((entry = zip.getNextEntry()) != null) {
//						if (entry.getName().endsWith(".java")) {
//							put(resourceInJarByPath, "/" + entry.getName(), source);
//						}
//					}
//				} finally {
//					zip.close();
//				}
//			}
		}
	}
	
	private Set<String> resourcesToIgnore = new HashSet<String>(Arrays.asList(
			"/git.properties",
			"/module.properties",
			"/log4j.properties",
			"/overview.html"
		));

	private void put(Map<String, File> map, String key, File jarFile) {
		if (key.startsWith("/META-INF/") || key.endsWith(".class") || resourcesToIgnore.contains(key)) {
			return;
		}
		
		File oldValue = map.put(key, jarFile);
		if (oldValue != null) {
			if (! oldValue.getAbsolutePath().replace("-sources.", ".").equals(jarFile.getAbsolutePath().replace("-sources.", "."))) {
				throw new IllegalStateException("Duplicate key " + key + " in " + jarFile + " and " + oldValue);
			}
		}
	}
	
	private void visitResources(File folder, String path, MigrationStat stat) throws Exception {
		if (! folder.exists()) {
			getLog().debug(folder + " does not exist");
			return;
		}
		
		File ignoreFolderFile = new File(folder.getParentFile(), folder.getName() + ignoreFileExtension);
		if (ignoreFolderFile.exists()) {
			getLog().debug("Ignore folder " + folder);
			stat.nbIgnored ++;
			return;
		}
		if (customizationFoldersToIgnore.contains(path)) {
			getLog().debug(folder + " already scanned as a custom package");
			return;
		}
		
		File[] children = folder.listFiles();
		
		// Fichiers avant folders
		for (File child : children) {
			String childPath = path + "/" + child.getName();
			if (! child.isDirectory()) {
				analyzeResource(child, childPath, stat);
			}
		}
		for (File child : children) {
			String childPath = path + "/" + child.getName();
			if (child.isDirectory()) {
				visitResources(child, childPath, stat);
			}
		}
	}

	private void analyzeResource(File file, String path, MigrationStat stat) throws Exception {
		if (path.startsWith("/extension")) {
			return;
		}
		
		// Gére customisation share
		if (path.startsWith("/alfresco/web-extension/site-data/extensions/") && path.endsWith(".xml")) {
			Extension extension = (Extension) unmarshaller.unmarshal(file);
			for (Module module : extension.modules.modules) {
				for (Customization customization : module.customizations.customizations) {
					customizationFoldersToIgnore.add("/alfresco/site-webscripts/" + customization.sourcePackageRoot.replace('.', '/'));

					File customPackage = new File(getBaseDir(), "src/main/resources/alfresco/web-extension/site-webscripts/" + customization.sourcePackageRoot.replace('.', '/'));
					String customPath = "/alfresco/site-webscripts/" + customization.targetPackageRoot.replace('.', '/');
					
					getLog().info("Custom package " + customization.targetPackageRoot);
					if (customPackage.exists()) {
						MigrationStat statModule = new MigrationStat();
						visitResources(customPackage, customPath, statModule);
						getLog().info("Custom package " + customization.targetPackageRoot + " stat: " + statModule.toString());
					} else {
						getLog().error(customPackage + " does not exist");
					}
				}
			}
			stat.nbModule ++;
			return;
		}

		String versionExtension = "." + alfrescoVersion + originalFileExtension;
		if (path.endsWith(originalFileExtension)) {
			if (path.endsWith(ignoreFileExtension)) {
				File patch = new File(file.getParentFile(), file.getName().substring(0, file.getName().length() - ignoreFileExtension.length()));
				if (! patch.exists()) {
					error(".ignore.ori file without corresponding patch file: " + file.getAbsolutePath());
				}
				if (file.length() > 0) {
					error(".ignore.ori file should be empty: " + file.getAbsolutePath());
				}
				stat.nbIgnored ++;
			} else if (path.endsWith(versionExtension)) {
				File patch = new File(file.getParentFile(), file.getName().substring(0, file.getName().length() - versionExtension.length()));
				if (! patch.exists()) {
					error("Original file without corresponding patch file: " + file.getAbsolutePath());
				} else if (Arrays.equals(FileUtils.readFileToByteArray(file), FileUtils.readFileToByteArray(patch))) {
					error("Patch identical to original. You may delete it : " + patch.getAbsolutePath());
				}
				stat.nbPatch ++;
			} else {
				File currentVersionOriginalFile = new File(file.getParentFile(), file.getName().substring(0, file.getName().length() - versionExtension.length()) + versionExtension);
				if (   deleteIdenticalResources
					&& currentVersionOriginalFile.exists() 
					&& Arrays.equals(FileUtils.readFileToByteArray(file), FileUtils.readFileToByteArray(currentVersionOriginalFile))) {
					getLog().info("Old resource identical to new resource, delete it : " + file.getAbsolutePath());
					file.delete();
				} else {
					error("You should migrate this original file to " + alfrescoVersion + " then delete it: " + file.getAbsolutePath());
				}
				stat.nbPatch ++;
			}
			return;
		}
		
		getLog().debug("Analyse " + path);
		String originalContent = findContentByPath(path);
		
		if (originalContent == null) {
			File ignore = new File(file.getParentFile(), file.getName() + ignoreFileExtension);
			if (ignore.exists()) {
				getLog().debug("Ignore resource " + file.getAbsolutePath());
				return;
			} else if (file.getName().toUpperCase().startsWith("README.")) {
				return;
			} else if (createMissingIgnoreFile) {
				getLog().warn("Create missing file " + ignore);
				FileUtils.write(ignore, "");
				return;
			} else {
				error("Original resource not found for " + file.getAbsolutePath() + ". Create a file with .ignore.ori suffix if it is not a Alfresco patch.");
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
	
	public static void main(String[] args) throws Exception {
		System.out.println(new File(".").getAbsolutePath());
		
		MigrationMojo mojo = new MigrationMojo();
		mojo.alfrescoVersion = "6.0.0";
		mojo.execute();
	}
}
