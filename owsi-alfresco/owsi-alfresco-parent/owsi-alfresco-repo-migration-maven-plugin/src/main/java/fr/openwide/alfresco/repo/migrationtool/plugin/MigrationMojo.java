package fr.openwide.alfresco.repo.migrationtool.plugin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
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
	private String enabled = "true";

	@Parameter(defaultValue="true")
	private boolean createMissingFile = true;
	
	@Parameter(defaultValue="false")
	private boolean createMissingIgnoreFile = false;
	
	@Parameter(defaultValue="true")
	private boolean deleteIdenticalResources = true;

	@Parameter(property="owsi.migration.overrideFile", defaultValue="")
	private String overrideFile;
	@Parameter(property="owsi.migration.overrideContent", defaultValue="")
	private String overrideContent;

	private Map<String, Artifact> resourceInJarByPath = new HashMap<String, Artifact>();
	private Map<String, Artifact> resourceInWarByPath = new HashMap<String, Artifact>();
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
		if (! isEnabled()) {
			getLog().warn("Migration tools disabled.");
			return;
		}
		try {
			@SuppressWarnings("unchecked")
			Set<Artifact> dependencyArtifacts = project.getDependencyArtifacts();
			
			getLog().debug("Artifacts : " + project.getDependencyArtifacts());
			
			for (Artifact artifact : dependencyArtifacts) {
				initDependency(artifact);
			}
			
			// parcours src/main/java/org/alfresco/
			MigrationStat stat = new MigrationStat();
			visitResources(new File(getBaseDir(), "src/main/resources/alfresco/web-extension/site-data/extensions/"), "/alfresco/web-extension/site-data/extensions/", stat);
			customizationFoldersToIgnore.add("/alfresco/web-extension");
			
			visitResources(new File(getBaseDir(), "src/main/resources/alfresco"), "/alfresco", stat);
			visitResources(new File(getBaseDir(), "src/main/webapp"), "", stat);
			visitResources(new File(getBaseDir(), "src/main/assembly/web"), "", stat);
			visitResources(new File(getBaseDir(), "src/main/assembly/config/alfresco/"), "/alfresco", stat);
			visitResources(new File(getBaseDir(), "src/main/java/org/alfresco"), "/org/alfresco", stat);
			getLog().info("Main stat " + stat.toString());
		} catch (Exception e) {
			throw new MojoExecutionException(e.toString(), e);
		}
		if (errors.length() > 0) {
			throw new MojoFailureException(errors.toString().substring(0, errors.length()-1));
		}
	}
	
	public boolean isEnabled() {
		String fileName = null;
		if ("true".equals(enabled)) {
			return true;
		} else if ("false".equals(enabled)) {
			return false;
		} else if ("once_an_day".equals(enabled)) {
			fileName = "yyyy.MM.dd";
		} else if ("once_an_hour".equals(enabled)) {
			fileName = "yyyy.MM.dd-hh";
		} else {
			fileName = enabled;
		}
		File folder = new File("/tmp/owsi.migration/" + project.getGroupId() + "/" + project.getArtifactId() + "/" + project.getVersion());
		folder.mkdirs();
		File file = new File(folder, new SimpleDateFormat(fileName).format(new Date()));
		try {
			return file.createNewFile();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	private void error(String msg) {
		getLog().error(msg);
		errors.append(msg).append("\n");
	}
	
	private void initDependency(Artifact artifact) throws Exception {
		File file = artifact.getFile();
		if (file != null) {
			ZipInputStream zip = new ZipInputStream(new BufferedInputStream(new FileInputStream(file)));
			try {
				ZipEntry entry;
				while ((entry = zip.getNextEntry()) != null) {
					if (! entry.isDirectory() && ! entry.getName().endsWith(".class")) {
						if (entry.getName().startsWith(WEB_INF_CLASSES)) {
							putDependency(resourceInWarByPath, entry.getName().substring(WEB_INF_CLASSES.length()), artifact, true, zip);
						} else if (entry.getName().startsWith("WEB-INF/lib")) {
							// TODO Ignore librairie externe pour le moment
						} else {
							putDependency(resourceInJarByPath, "/" + entry.getName(), artifact, false, zip);
						}
					}
				}
			} finally {
				zip.close();
			}
		}
	}
	
	private Set<String> resourcesToIgnore = new HashSet<String>(Arrays.asList(
			"/git.properties",
			"/module.properties",
			"/log4j.properties",
			"/overview.html"
		));

	private void putDependency(Map<String, Artifact> map, String key, Artifact artifact, boolean inWebInfClasses, InputStream in) throws IOException {
		if (key.startsWith("/META-INF/") || key.endsWith(".class") || resourcesToIgnore.contains(key)) {
			return;
		}
		
		Artifact oldValue = map.put(key, artifact);
		if (oldValue != null) {
			if (! oldValue.getFile().getAbsolutePath().replace("-sources.", ".").equals(artifact.getFile().getAbsolutePath().replace("-sources.", "."))) {
				throw new IllegalStateException("Duplicate key " + key + " in " + artifact.getFile() + " and " + oldValue);
			}
		}
		
		if (overrideFile != null && overrideFile.length() > 0 && key.contains(overrideFile)) {
			File destinationFile = createOverrideFile(key, artifact, inWebInfClasses);
			OutputStream output = FileUtils.openOutputStream(destinationFile);
			try {
				IOUtils.copy(in, output);
			} finally {
				IOUtils.closeQuietly(output);
			}
		}

		if (overrideContent != null && overrideContent.length() > 0) {
			String fileContent = IOUtils.toString(in);
			if (fileContent.contains(overrideContent)) {
				File destinationFile = createOverrideFile(key, artifact, inWebInfClasses);
				FileUtils.write(destinationFile, fileContent);
			}
		}
	}
	
	private File createOverrideFile(String key, Artifact artifact, boolean inWebInfClasses) {
		File destinationRoot = new File(getBaseDir(),
			("war".equals(artifact.getType()) && ! inWebInfClasses) 
				? "src/main/webapp"
				: key.endsWith(".java") ? "src/main/java" : "src/main/resources");
		File destinationFile = new File(destinationRoot, key);
		destinationFile.getParentFile().mkdirs();
		getLog().info("File " + destinationFile + " created");
		return destinationFile;
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

		// Si fini par .ori
		if (path.endsWith(originalFileExtension)) {
			//String versionExtension = "." + alfrescoVersion + originalFileExtension;
			
			// Si fini par .ignore.ori
			if (path.endsWith(ignoreFileExtension)) {
				File patch = new File(file.getParentFile(), file.getName().substring(0, file.getName().length() - ignoreFileExtension.length()));
				if (! patch.exists()) {
					error(".ignore.ori file without corresponding patch file: " + file.getAbsolutePath());
				}
				if (file.length() > 0) {
					error(".ignore.ori file should be empty: " + file.getAbsolutePath());
				}
				stat.nbIgnored ++;
			} else if (path.contains(versionSeparator)) {
				String fileVersion = path.substring(path.lastIndexOf(versionSeparator) + versionSeparator.length(), path.length() - originalFileExtension.length());
				File patchFile = new File(file.getParentFile(), file.getName().substring(0, file.getName().lastIndexOf(versionSeparator)));
				
				String currentVersion = findVersionByPath(path.substring(0, path.lastIndexOf(versionSeparator)));
				byte[] originalContent = findContentByPath(path.substring(0, path.lastIndexOf(versionSeparator)));
				if (originalContent == null) {
					error("Can not find original content for " + file.getAbsolutePath());
					return;
				}
				
				if (currentVersion.equals(fileVersion)) {
					if (! Arrays.equals(originalContent, FileUtils.readFileToByteArray(file))) {
						error("Copy different from original " + file.getAbsolutePath());
						return;
					}
					
					if (! patchFile.exists()) {
						error("Original file without corresponding patch file: " + file.getAbsolutePath());
					} else if (Arrays.equals(FileUtils.readFileToByteArray(file), FileUtils.readFileToByteArray(patchFile))) {
						error("Patch identical to original. You may delete it : " + patchFile.getAbsolutePath());
					}
				} else {
					File currentVersionOriginalFile = new File(file.getParentFile(), file.getName().substring(0, file.getName().lastIndexOf(versionSeparator))
							+ versionSeparator + currentVersion + originalFileExtension);
					if (   deleteIdenticalResources
						&& currentVersionOriginalFile.exists() 
						&& Arrays.equals(FileUtils.readFileToByteArray(file), FileUtils.readFileToByteArray(currentVersionOriginalFile))) {
						getLog().info("Old resource identical to new resource, delete it : " + file.getAbsolutePath());
						file.delete();
					} else {
						error("You should migrate this original file to the current version then delete it: " + file.getAbsolutePath());
					}
				}
				stat.nbPatch ++;
			}
		} else {
			getLog().debug("Analyse " + path);
			byte[] originalContent = findContentByPath(path);
			String version = findVersionByPath(path);
			
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
			
			if (path.endsWith(".js") && ! path.endsWith("-min.js")) {
				String pathMin = path.substring(path.lastIndexOf(".js")) + "-min.js";
				if (findContentByPath(pathMin) != null) {
					// TODO compress avec https://search.maven.org/artifact/com.yahoo.platform.yui/yuicompressor/2.4.8/jar
					// http://yui.github.io/yuicompressor/
					File fileMin = new File(file.getParentFile(), file.getName().substring(file.getName().lastIndexOf(".js")) + "-min.js");
					FileUtils.copyFile(file, fileMin);
				}
			}
			
			File original = new File(file.getParentFile(), file.getName() + versionSeparator + version + originalFileExtension);
			if (original.exists()) {
				byte[] copyContent = FileUtils.readFileToByteArray(original);
				if (! Arrays.equals(originalContent, copyContent)) {
					error("Copy file different from original " + original.getAbsolutePath());
				}
				
				byte[] patchedContent = FileUtils.readFileToByteArray(file);
				if (Arrays.equals(originalContent, patchedContent)) {
					error("unecessary patch file. Identical resource:" + file.getAbsolutePath());
				}
			} else {
				if (createMissingFile) {
					getLog().warn("Create missing file " + original.getAbsolutePath());
					FileUtils.writeByteArrayToFile(original, originalContent);
				} else {
					error("Copy file not found and createMissingFile=false: " + original.getAbsolutePath());
				}
			}
		}
	}
	
	private String findVersionByPath(String path) throws Exception {
		if (path.startsWith("/alfresco/extension/templates/")) {
			path = path.replace("/alfresco/extension/templates/", "/alfresco/templates/");
		}
		
		Artifact war = resourceInWarByPath.get(path);
		if (war != null) {
			return war.getVersion();
		}
		
		Artifact jar = resourceInJarByPath.get(path);
		if (jar != null) {
			return jar.getVersion();
		}
		return null;
	}
	
	private byte[] findContentByPath(String path) throws Exception {
		if (path.startsWith("/alfresco/extension/templates/")) {
			path = path.replace("/alfresco/extension/templates/", "/alfresco/templates/");
		}
		
		Artifact war = resourceInWarByPath.get(path);
		if (war != null) {
			ZipInputStream zip = new ZipInputStream(new BufferedInputStream(new FileInputStream(war.getFile())));
			try {
				ZipEntry entry;
				while ((entry = zip.getNextEntry()) != null) {
					if (entry.getName().equals(WEB_INF_CLASSES + path)) {
						return IOUtils.toByteArray(zip);
					}
				}
			} finally {
				zip.close();
			}
			throw new IllegalStateException(path + " - " + war.getFile().getAbsolutePath());
		}
		
		Artifact jar = resourceInJarByPath.get(path);
		if (jar != null) {
			ZipInputStream zip = new ZipInputStream(new BufferedInputStream(new FileInputStream(jar.getFile())));
			try {
				ZipEntry entry;
				while ((entry = zip.getNextEntry()) != null) {
					if (entry.getName().equals(path.substring("/".length()))) {
						return IOUtils.toByteArray(zip);
					}
				}
			} finally {
				zip.close();
			}
			throw new IllegalStateException(path + " - " + jar.getFile().getAbsolutePath());
		}
		return null;
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(new File(".").getAbsolutePath());
		
		MigrationMojo mojo = new MigrationMojo();
		mojo.execute();
	}
}
