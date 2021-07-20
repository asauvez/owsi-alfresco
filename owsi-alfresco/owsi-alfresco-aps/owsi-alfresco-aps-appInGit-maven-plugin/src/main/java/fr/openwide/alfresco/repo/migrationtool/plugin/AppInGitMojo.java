package fr.openwide.alfresco.repo.migrationtool.plugin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.json.JSONObject;

@Mojo(name="appInGit", defaultPhase=LifecyclePhase.COMPILE)
public class AppInGitMojo extends AbstractMojo {

	@Parameter(readonly=true, defaultValue="${project}")
	protected MavenProject project;
	
	@Parameter(defaultValue="http://localhost:9080/activiti-app")
	private String targetUrl = "http://localhost:9080/activiti-app";

	@Parameter(defaultValue="admin@app.activiti.com")
	private String login = "admin@app.activiti.com";

	@Parameter(defaultValue="admin")
	private String password = "admin";

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		File srcActivitiFolder = new File(getBaseDir(), "src/activiti/");
		
		if (srcActivitiFolder.exists()) {
			for (File app : srcActivitiFolder.listFiles()) {
				File srcFolder = new File(getBaseDir(), "src/activiti/" + app.getName());
				File zipFile = new File(getBaseDir(), "target/activiti/" + app.getName() + ".zip");

				getLog().info("Create export " + zipFile);
				zipFile.getParentFile().mkdirs();
				try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile))) {
					File[] children = srcFolder.listFiles();
					for (File childFile : children) {
						zipFile(childFile, childFile.getName(), zipOut);
					}
				} catch (IOException e) {
					throw new MojoExecutionException(e.toString(), e);
				}
			}
		} else {
			// Obligé d'aller tester l'existance des apps une par une car 
			// /activiti-app/app/rest/runtime/app-definitions ne permet pas le Basic Auth
			// et /activiti-app/api/enterprise/app-definitions n'existe pas
			for (int i = 3; i < 10; i++) {
				try {
					String url = targetUrl + "/api/enterprise/app-definitions/" + i;
					JSONObject res = callWS(new HttpGet(url));
					int applId = res.getInt("id");
					String name = res.getString("name");
					exportApp(applId, name);
				} catch (IOException e) {
					throw new MojoExecutionException(e.toString(), e);
				} catch (Erreur404Exception e) {
					// Ignore
				}
			}
		}
	}
	
	private void exportApp(int appId, String name) throws IOException, Erreur404Exception {
		getLog().info("Manage application " + appId + " : '" + name + "'");
		name = name.replace(' ', '-');

		File srcFolder = new File(getBaseDir(), "src/activiti/" + name);
		File zipFile = new File(getBaseDir(), "target/activiti/" + name + ".zip");

		String url = targetUrl + "/api/enterprise/app-definitions/" + appId + "/export";
		getLog().info("Call to GET " + url);
		getLog().info("Import processus to " + srcFolder);
		
		zipFile.getParentFile().mkdirs();
		try (OutputStream out = new FileOutputStream(zipFile)) {
			callWS(new HttpGet(url), out);
			out.flush();
		}
		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
			ZipEntry zipEntry = zis.getNextEntry();
			while (zipEntry != null) {
				File newFile = new File(srcFolder, zipEntry.getName());
				if (zipEntry.isDirectory()) {
					newFile.mkdirs();
				} else {
					newFile.getParentFile().mkdirs();
					if (! zipEntry.getName().endsWith(".png")) {
						try (OutputStream out = new FileOutputStream(newFile)) {
							IOUtils.copy(zis, out);
						}
					}
				}
				zipEntry = zis.getNextEntry();
			}
		}
	}
	
	@SuppressWarnings("serial")
	public static class Erreur404Exception extends Exception {}
	
	private void callWS(HttpUriRequest request, OutputStream out) throws IOException, Erreur404Exception {
		CredentialsProvider provider = new BasicCredentialsProvider();
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(login, password);
		provider.setCredentials(AuthScope.ANY, credentials);
		 
		CloseableHttpClient httpClient = HttpClientBuilder.create()
			.setDefaultCredentialsProvider(provider)
			.build();
		try (CloseableHttpResponse response = httpClient.execute(request)) {
			if (response.getStatusLine().getStatusCode() == 404) {
				throw new Erreur404Exception();
			} else if (response.getStatusLine().getStatusCode() != 200) {
				throw new IOException(response.getStatusLine().toString());
			}
			response.getEntity().writeTo(out);
		}
	}
	
	private JSONObject callWS(HttpUriRequest request) throws IOException, Erreur404Exception {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			callWS(request, out);
			return new JSONObject(new String(out.toByteArray(), "UTF-8"));
		}
	}
	
	private void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
		if (fileToZip.isHidden()) {
			return;
		}
		if (fileToZip.isDirectory()) {
			if (fileName.endsWith("/")) {
				zipOut.putNextEntry(new ZipEntry(fileName));
				zipOut.closeEntry();
			} else {
				zipOut.putNextEntry(new ZipEntry(fileName + "/"));
				zipOut.closeEntry();
			}
			File[] children = fileToZip.listFiles();
			for (File childFile : children) {
				zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
			}
			return;
		}
		try (FileInputStream fis = new FileInputStream(fileToZip)) {
			ZipEntry zipEntry = new ZipEntry(fileName);
			zipOut.putNextEntry(zipEntry);
			byte[] bytes = new byte[1024];
			int length;
			while ((length = fis.read(bytes)) >= 0) {
				zipOut.write(bytes, 0, length);
			}
		}
	}
	
	private File getBaseDir() {
		return (project != null) ? project.getBasedir() : new File(".");
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(new File(".").getAbsolutePath());
		
		AppInGitMojo mojo = new AppInGitMojo();
		mojo.execute();
	}
}
