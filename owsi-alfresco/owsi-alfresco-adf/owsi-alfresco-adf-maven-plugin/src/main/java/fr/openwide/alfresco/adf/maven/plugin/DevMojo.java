package fr.openwide.alfresco.adf.maven.plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.ProcessBuilder.Redirect;
import java.net.URL;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
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
 * TODO
 * 
 * @author asauvez
 */
@Mojo(name="dev", defaultPhase=LifecyclePhase.COMPILE)

public class DevMojo extends AbstractAdfMojo {


	private static final String ORI_EXTENSION = ".ori";

	private static final long ORIGINAL_TIMESPAMP = 0L;

	@Parameter
	private String appUrl = "https://github.com/Alfresco/alfresco-content-app/archive/{0}.zip";
	
	@Parameter
	private String appVersion = "v1.9.0";

	private Set<File> filesToIgnore = new HashSet<>();

	public DevMojo() throws Exception {}
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			File targetAppFolder = getTargetAppFolder();
			File rootAppFolder = getRootSrcAppFolder();
			File originalAppFolder = getOriginalAppFolder();
			
			replaceInFile("src/assets/app.extensions.json", 
					"", "\"$references\": [", 
					LINE_BEFORE + "\"" + getSourceCustomExtensionsSourceFile().getName() + "\", " + LINE_AFTER);
			String moduleFileName = getSourceCustomModuleFile().getName();
			replaceInFile("src/app/app.module.ts", 
					"import { " + getCustomModuleName() + " } from './" + moduleFileName.substring(0, moduleFileName.length() - ".ts".length()) + "';\n",
					"  imports: [", 
					"  imports: [\n    " + getCustomModuleName() + ",\n");
//			replaceInFile("angular.json", "",
//					"      \"root\": \"\",", 
//					"      \"root\": \"" + project.getBuild().getFinalName() + "\",");
//			replaceInFile("src/index.html", "",
//					"<head>", 
//					"<head>\n    <base href=\"" + project.getBuild().getFinalName() + "\" />");

			createFileLinks(rootAppFolder, targetAppFolder, originalAppFolder, true);
			//createFileLinks(rootAppFolder, targetAppFolder);
			getLog().info("Getting command...");
			String serve = System.getProperty("serve");
			getLog().info("Command is " + serve);
			
			if (serve != null) {
				Process process = new ProcessBuilder(serve.split(" "))
					.redirectOutput(Redirect.INHERIT)
					.redirectError(Redirect.INHERIT)
					.start();
				Runtime.getRuntime().addShutdownHook(new Thread() {
					public void run() {
						process.destroy();
					}
				});
				while(true) {
					//getLog().info("Running...");
					createFileLinks(rootAppFolder, targetAppFolder, originalAppFolder, false);
					createTargetLinks(targetAppFolder,rootAppFolder);
					TimeUnit.SECONDS.sleep(4);
					//if (!p.isAlive()) break;
				}
				//process.destroy();
			}
		} catch (IOException | InterruptedException e) {
			throw new IllegalStateException(e);
		} 
	}
	
	// This function adds target files to the src folder, only if they have been recently modified
	private void createTargetLinks(File src, File dest) throws IOException {
		if (src.isDirectory()) {
			//dest.mkdir();
			for (File file : src.listFiles()) {
				createTargetLinks(file, new File(dest, file.getName()));
			}
		} else {
			Date date = new Date();
			long time = date.getTime();
			long lastModified = src.lastModified();
			if(time - lastModified < 4500) {
				if(dest.exists() && ! dest.delete()) {
					throw new IllegalStateException("Ne peut pas effacer " + dest);
				}
				dest.getParentFile().mkdirs();
				getLog().info("Create target link from " + src);
				Files.copy(src.toPath(), dest.toPath());
				dest.setLastModified(lastModified - 8000);
				
			}
		}
	}

	private void createFileLinks(File src, File target, File ori, boolean isFirstTime) throws IOException {
		if (src.isDirectory()) {
			target.mkdir();
			for (File file : src.listFiles()) {
				createFileLinks(file, new File(target, file.getName()), new File(ori, file.getName()), isFirstTime);
			}
		} else {
			if (   src.getName().endsWith(ORI_EXTENSION)
				|| filesToIgnore.contains(src)
				|| filesToIgnore.contains(target)) {
				// ignore
			} else if(target.exists() && !isFirstTime) {
				Date date = new Date();
				long time = date.getTime();
				long lastModified = src.lastModified();
				if(time - lastModified < 4500) {
					if(! target.delete()) {
						throw new IllegalStateException("Ne peut pas effacer " + target);
					}
					getLog().info("Update link " + src + " last modif: " + (time - lastModified) + " ms ago");
					
					Files.copy(src.toPath(), target.toPath());
					target.setLastModified(lastModified - 8000);
				}else {
					//getLog().info("File " + src + " was changed too recently " +(lastModified - time) +" ms");
				}
			} else if(src.exists() && ori.exists() && isFirstTime) {
				File oriFile = new File(src.getAbsolutePath() + "." + appVersion + ORI_EXTENSION);
				oriFile.delete();
				Files.copy(ori.toPath(), oriFile.toPath());
				getLog().info("Create new link " + src /**+ " to dest "+ dest +" dest exists:" + dest.getAbsoluteFile().exists()*/);
				target.delete();
				Files.copy(src.toPath(), target.toPath());
			} else {
				getLog().info("Create new link " + src /**+ " to dest "+ dest +" dest exists:" + dest.getAbsoluteFile().exists()*/);
				target.delete();
				Files.copy(src.toPath(), target.toPath());
			}
		}
	}
	
	// Répertoire pour stocker la version github du projet
	// /tmp/adf-app-cache/https___github_com_Alfresco_alfresco_content_app_archive_v1_9_0_zip/alfresco-content-app-1.9.0/
	private File getOriginalAppFolder() throws IOException {
		File tmp = new File(System.getProperty("java.io.tmpdir"), "adf-app-cache");
		URL url = new URL(MessageFormat.format(appUrl, appVersion));
		File originalAppFolder = new File(tmp, url.toString().replaceAll("[^0-9a-zA-Z]", "_"));
		if (! originalAppFolder.exists()) {
			getLog().info("Téléchargement de " + url);
			
			try (ZipInputStream zin = new ZipInputStream(url.openConnection().getInputStream())) {
				ZipEntry entry;
				while((entry = zin.getNextEntry()) != null) {
					File outFile = new File(originalAppFolder, entry.getName());
					if (entry.isDirectory()) {
						outFile.mkdirs();
					} else {
						outFile.getParentFile().mkdirs();
						try (OutputStream out = new FileOutputStream(outFile)) {
							 IOUtils.copy(zin, out);
						}
					}
					outFile.setLastModified(ORIGINAL_TIMESPAMP);
				}
			}
		}
		
		// Saute le premier niveau
		File[] files = originalAppFolder.listFiles();
		if (files.length != 1) {
			throw new IllegalStateException(Arrays.asList(files).toString());
		}
		return files[0];
	}
	
	// Répertoire pour stocker l'application modifiée complete
	// ./target/orginal-app/
	private File getTargetAppFolder() throws IOException {
		File targetAppFolder = new File(getBaseDir(), "target/original-app");
		if (! targetAppFolder.exists()) {
			getLog().info("Création de " + targetAppFolder);
			File originalAppFolder = getOriginalAppFolder();
			FileUtils.copyDirectory(originalAppFolder, targetAppFolder);
		}
		return targetAppFolder;
	}
	
	// ./src/app/
	private File getRootSrcAppFolder() {
		File folder = new File(getBaseDir(), "src/app");
		folder.mkdirs();
		return folder;
	}
	
	
	// src/assets/plugins/xxx.json
	private File getSourceCustomExtensionsSourceFile() throws IOException {
		File customExtensionsSourceFile = new File(getRootSrcAppFolder(), "src/assets/plugins/" + project.getArtifactId() + ".json");
		filesToIgnore.add(customExtensionsSourceFile);
		
		if (! customExtensionsSourceFile.exists()) {
			getLog().info("Create" + customExtensionsSourceFile);

			customExtensionsSourceFile.getParentFile().mkdirs();
			try (PrintWriter writer = new PrintWriter(new FileWriter(customExtensionsSourceFile))) {
				writer.println("{");
				writer.println("  \"$schema\": \"../../../extension.schema.json\",");
				writer.println("  \"$id\": \"" + project.getArtifactId() + "\",");
				writer.println("  \"$name\": \"" + project.getName() + "\",");
				writer.println("  \"$version\": \"" + project.getVersion() + "\",");
				writer.println("  \"$description\": \"Permet de surcharger les valeurs de app.extensions.json\"");
				writer.println("}");
				writer.flush();
			}
		}
		return customExtensionsSourceFile;
	}
	
	private static final String LINE_BEFORE = "##LINE_BEFORE##";
	private static final String LINE_AFTER = "##LINE_AFTER##";
	
	private File replaceInFile(String path, String header, String linePattern, String lineReplacement) throws IOException {
		File source = new File(getOriginalAppFolder(), path);
		File dest = new File(getTargetAppFolder(), path);
		
		try (BufferedReader reader = new BufferedReader(new FileReader(source))) {
			try (PrintWriter writer = new PrintWriter(new FileWriter(dest))) {
				getLog().info("Patch " + dest);
				
				writer.append(header);

				String line;
				while ((line = reader.readLine()) != null) {
					int pos = line.indexOf(linePattern);
					if (pos != -1) {
						line = lineReplacement
							.replace(LINE_BEFORE, line.substring(0, pos + linePattern.length()))
							.replace(LINE_AFTER, line.substring(pos + linePattern.length()));
					}
					writer.println(line);
				}
			}
		}
		
		filesToIgnore.add(dest);
		return dest;
	}
	
	// ./src/app/src/app/xxx.module.ts
	private File getSourceCustomModuleFile() throws IOException {
		File customModuleFile = new File(getRootSrcAppFolder(), "src/app/" + project.getArtifactId() + ".module.ts");
		filesToIgnore.add(customModuleFile);
		if (! customModuleFile.exists()) {
			getLog().info("Create" + customModuleFile);

			customModuleFile.getParentFile().mkdirs();
			try (PrintWriter writer = new PrintWriter(new FileWriter(customModuleFile))) {
				writer.println("import { NgModule } from '@angular/core';");
				writer.println("@NgModule({");
				writer.println("  imports: [],");
				writer.println("  exports: [],");
				writer.println("  declarations: [],");
				writer.println("  providers: [],");
				writer.println("  entryComponents: []");
				writer.println("})");
				writer.println("export class " + getCustomModuleName() + " {}");
			}
		}
		return customModuleFile;
	}
	// xxxModule
	private String getCustomModuleName() {
		return project.getArtifactId().replace("-", "") + "Module";
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(new File(".").getAbsolutePath());
		
		DevMojo mojo = new DevMojo();
		mojo.execute();
	}
}
