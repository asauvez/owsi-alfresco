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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;
/**
 * TODO
 * 
 * @author asauvez
 */
@Mojo(name="dev", defaultPhase=LifecyclePhase.COMPILE)

public class DevMojo extends AbstractAdfMojo {


	private static final String ORI_EXTENSION = ".ori";

	private static final String REFERENCES = "\"$references\": [";

	private static final long ORIGINAL_TIMESPAMP = 0L;

	@Parameter
	private String appUrl = "https://github.com/Alfresco/alfresco-content-app/archive/{0}.zip";
	
	@Parameter
	private String appVersion = "v1.9.0";

	public DevMojo() throws Exception {}
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			File targetAppFolder = getTargetAppFolder();
			File rootAppFolder = getRootSrcAppFolder();
			initAppExtensionsFile();
			createFileLinks(rootAppFolder, targetAppFolder, true);
			//createFileLinks(rootAppFolder, targetAppFolder);
			getLog().info("Getting command...");
			String serve = System.getProperty("serve");
			getLog().info("Command is " + serve);
			
			
			
			if (serve != null) {
				try {
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
						createFileLinks(rootAppFolder, targetAppFolder, false);
						createTargetLinks(targetAppFolder,rootAppFolder);
						TimeUnit.SECONDS.sleep(4);
						//if (!p.isAlive()) break;
					}
					//process.destroy();
				}catch(Throwable t){
					t.printStackTrace();
				}
				
			}
			


		} catch (IOException e) {
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

	private void createFileLinks(File src, File dest, boolean isFirstTime) throws IOException {

		if (src.isDirectory()) {
			dest.mkdir();
			for (File file : src.listFiles()) {
				createFileLinks(file, new File(dest, file.getName()), isFirstTime);
			}
		} else {
			if (src.getName().endsWith(ORI_EXTENSION)) {
				// ignore
			} else if(dest.exists() && !isFirstTime) {
				Date date = new Date();
				long time = date.getTime();
				long lastModified = src.lastModified();
				if(time - lastModified < 4500) {
					if(! dest.delete()) {
						throw new IllegalStateException("Ne peut pas effacer " + dest);
					}
					getLog().info("Update link " + src + " last modif: " + (time - lastModified) + " ms ago");
					
					Files.copy(src.toPath(), dest.toPath());
					dest.setLastModified(lastModified - 8000);
				}else {
					//getLog().info("File " + src + " was changed too recently " +(lastModified - time) +" ms");
				}
			} else if(dest.exists() && isFirstTime) {
				File oriFile = new File(src.getAbsolutePath() + "." + appVersion + ORI_EXTENSION);
				oriFile.delete();
				dest.renameTo(oriFile);
				getLog().info("Create new link " + src /**+ " to dest "+ dest +" dest exists:" + dest.getAbsoluteFile().exists()*/);
				Files.copy(src.toPath(), dest.toPath());
			} else {
				getLog().info("Create new link " + src /**+ " to dest "+ dest +" dest exists:" + dest.getAbsoluteFile().exists()*/);
				Files.copy(src.toPath(), dest.toPath());
			}
		}
	}
	
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
	
	private File getTargetAppFolder() throws IOException {
		File targetAppFolder = new File(getBaseDir(), "target/original-app");
		if (! targetAppFolder.exists()) {
			getLog().info("Création de " + targetAppFolder);
			File originalAppFolder = getOriginalAppFolder();
			FileUtils.copyDirectory(originalAppFolder, targetAppFolder);
		}
		return targetAppFolder;
	}
	
	private File getRootSrcAppFolder() {
		File folder = new File(getBaseDir(), "src/app");
		folder.mkdirs();
		return folder;
	}
	
	private File getCustomExtensionsSourceFile() throws IOException {
		File customExtensionsSourceFile = new File(getRootSrcAppFolder(), "src/assets/plugins/" + project.getArtifactId() + ".json");
		if (! customExtensionsSourceFile.exists()) {
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
	
	private void initAppExtensionsFile() throws IOException {
		File originalAppExtensionsFile = new File(getOriginalAppFolder(), "src/assets/app.extensions.json");
		try (BufferedReader reader = new BufferedReader(new FileReader(originalAppExtensionsFile))) {
			
			File appExtensionsFile = new File(getTargetAppFolder(), "src/assets/app.extensions.json");
			try (PrintWriter writer = new PrintWriter(new FileWriter(appExtensionsFile))) {
				String line;
				while ((line = reader.readLine()) != null) {
					int pos = line.indexOf(REFERENCES);
					if (pos != -1) {
						line = line.substring(0, pos + REFERENCES.length())
							+ "\"" + getCustomExtensionsSourceFile().getName() + "\", "
							+ line.substring(pos + REFERENCES.length());
					}
					writer.println(line);
				}
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(new File(".").getAbsolutePath());
		
		DevMojo mojo = new DevMojo();
		mojo.execute();
	}
}
