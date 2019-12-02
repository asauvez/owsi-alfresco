package fr.openwide.alfresco.adf.maven.plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
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

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * TODO
 * 
 * @author asauvez
 */
@Mojo(name="dev", defaultPhase=LifecyclePhase.COMPILE)
public class DevMojo extends AbstractAdfMojo {


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
			File srcAppFolder = getSrcAppFolder();
			File rootAppFolder = getRootAppFolder();
			
			targetAppFolder.mkdirs();
			srcAppFolder.mkdirs();
			rootAppFolder.mkdirs();
			
			FileUtils.copyDirectory(srcAppFolder, new File(targetAppFolder, "src"));
			FileUtils.copyDirectory(rootAppFolder, targetAppFolder);
		} catch (IOException e) {
			throw new IllegalStateException(e);
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
		return originalAppFolder;
	}
	
	private File getTargetAppFolder() throws IOException {
		File targetAppFolder = new File(getBaseDir(), "target/orginal-app");
		if (! targetAppFolder.exists()) {
			getLog().info("Création de " + targetAppFolder);
			File originalAppFolder = getOriginalAppFolder();
			// Saute le premier niveau
			File[] files = originalAppFolder.listFiles();
			if (files.length != 1) {
				throw new IllegalStateException(Arrays.asList(files).toString());
			}
			FileUtils.copyDirectory(files[0], targetAppFolder);
		}
		return targetAppFolder;
	}
	
	private File getSrcAppFolder() {
		return new File(getBaseDir(), "src/app/src");
	}
	private File getRootAppFolder() {
		return new File(getBaseDir(), "src/app/root");
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(new File(".").getAbsolutePath());
		
		DevMojo mojo = new DevMojo();
		mojo.execute();
	}
}
