package fr.openwide.alfresco.test.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AlfrescoRestClient {
	
	private Properties properties = new Properties();

	private CloseableHttpClient client;
	
	private ObjectMapper mapper = new ObjectMapper();
		
	public AlfrescoRestClient(String applicationName) {
		try {
			// Lit les valeurs incluses dans le WAR
			try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("test.properties")) {
				properties.load(in);
			}
			
			// Ecrase avec les valeurs de alfresco/tomcat/shared/classes/<application>-tests.properties
			try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(applicationName + "-tests.properties")) {
				if (in != null) {
					properties.load(in);
				}
			}
			
			client = HttpClientBuilder.create()
				.disableRedirectHandling()
				// Ignore problème de certificat
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
				.setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
						@Override
						public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
							return true;
						}
					}).build())
				.build();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
	
	public String getTechnicalUser() {
		return properties.getProperty("test.login", "admin");
	}
	
	public String getBaseUrl() {
		return properties.getProperty("test.host", "http://localhost:8080/alfresco");
	}

	public ObjectMapper getMapper() {
		return mapper;
	}
	
	private CloseableHttpResponse executeRequest(HttpUriRequest request) {
		String login = getTechnicalUser();
		String password = properties.getProperty("test.password", "admin");
		
		request.setHeader("Content-type", "application/json");
		request.setHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString((login + ":" + password).getBytes()));

		try {
			CloseableHttpResponse response = client.execute(request);
			
			long waitSeconds = Long.parseLong(properties.getProperty("test.wait.seconds", "0"));
			if (waitSeconds > 0L) {
				System.out.println("Attente de " + waitSeconds + " secondes...");
				try {
					Thread.sleep(waitSeconds*1000);
				} catch (InterruptedException e) {
					throw new IllegalStateException(e);
				}
			}
			return response;
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public class ErrorStatusException extends RuntimeException {
		private CloseableHttpResponse response;

		public ErrorStatusException(CloseableHttpResponse response) {
			super(response.getStatusLine().toString() + "\n" + convertResponseToObject(response, String.class));
			this.response = response;
		}
		public int getStatus() {
			return response.getStatusLine().getStatusCode();
		}
	}
	
	private void checkResponseStatus(CloseableHttpResponse response) {
		switch (response.getStatusLine().getStatusCode()) {
		case 200:
		case 201:
			break;
		default:
			throw new ErrorStatusException(response);
		}
	}
	public <T> T request(HttpRequestBase request, Class<T> returnType) {
		try (CloseableHttpResponse response = executeRequest(request)) {
			checkResponseStatus(response);
			return convertResponseToObject(response, returnType);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	public <T> T request(HttpRequestBase request, JavaType returnType) {
		try (CloseableHttpResponse response = executeRequest(request)) {
			checkResponseStatus(response);
			return convertResponseToObject(response, returnType);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private <T> int requestResponseStatus(HttpRequestBase httpRequest) {
		try (CloseableHttpResponse response = executeRequest(httpRequest)) {
			return response.getStatusLine().getStatusCode();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public String redirect(String url) {
		HttpGet httpRequest = new HttpGet(getBaseUrl() + url);
		
		try (CloseableHttpResponse response = executeRequest(httpRequest)) {
			switch (response.getStatusLine().getStatusCode()) {
			case 302:
				break;
			default:
				String valueAsString = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
				throw new IllegalStateException(response.getStatusLine().toString() + "\n" + valueAsString);
			}

			return response.getFirstHeader("Location").getValue();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public HttpPost postRequest(String url, Object objectJson) {
		String jsonString = convertObjectToJson(objectJson);
				
		HttpPost httpPost = new HttpPost(getBaseUrl() + url);
		httpPost.setHeader("Accept", "application/json");

		try {
			StringEntity entity = new StringEntity(jsonString);
			httpPost.setEntity(entity);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
		
		return httpPost;
	}

	public HttpPut putRequest(String url, Object objectJson) {
		String jsonString = convertObjectToJson(objectJson);
				
		HttpPut httpPut = new HttpPut(getBaseUrl() + url);
		httpPut.setHeader("Accept", "application/json");

		try {
			StringEntity entity = new StringEntity(jsonString);
			httpPut.setEntity(entity);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
		
		return httpPut;
	}

	public HttpGet getRequest(String url) {
		return new HttpGet(getBaseUrl() + url);
	}

	public <T> int getRequestResponseStatus(String url) {
		HttpGet httpGet = new HttpGet(getBaseUrl() + url);
		return requestResponseStatus(httpGet);
	}
		
	private String convertObjectToJson (Object object) {
		if (object == null) return "";
		
		try {
			String jsonString = mapper.writeValueAsString(object);
			System.out.println("--> " + jsonString);
			return jsonString;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> T convertResponseToObject(CloseableHttpResponse response, Class<T> className) {
		try (InputStream in = response.getEntity().getContent()) {
			byte[] b = IOUtils.toByteArray(in);
			if (className == byte[].class) {
				return (T) b;
			}
			
			String valueAsString = new String(b, "UTF-8");
			System.out.println("<-- " + valueAsString);
			
			if(className == String.class) {
				return (T) valueAsString;
			}
			ObjectMapper mapper = new ObjectMapper();
			try {
				return mapper.readValue(valueAsString, className);
			}catch (Exception e) {
				e.printStackTrace();
				throw new IllegalStateException("Erreur lors de la conversion de la chaine "+ valueAsString +" en objet de la classe "+className, e);
			}
		} catch (UnsupportedOperationException | IOException e1) {
			throw new IllegalStateException(e1);
		}
	}
	
	public <T> T convertResponseToObject(CloseableHttpResponse response, JavaType javaType) {
		try (InputStream in = response.getEntity().getContent()) {
			byte[] b = IOUtils.toByteArray(in);
			String valueAsString = new String(b, "UTF-8");
			System.out.println("<-- " + valueAsString);
			
			ObjectMapper mapper = new ObjectMapper();
			try {
				return mapper.readValue(valueAsString, javaType);
			}catch (Exception e) {
				e.printStackTrace();
				throw new IllegalStateException("Erreur lors de la conversion de la chaine "+ valueAsString +" en objet de la classe "+javaType, e);
			}
		} catch (UnsupportedOperationException | IOException e1) {
			throw new IllegalStateException(e1);
		}
	}
	
	public void delete(String url) {
		HttpDelete httpDelete = new HttpDelete(getBaseUrl() + url);

		try {
			CloseableHttpResponse response = executeRequest(httpDelete);
			switch (response.getStatusLine().getStatusCode()) {
			case 204:
				break;
			default:
				String valueAsString = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
				throw new IllegalStateException(response.getStatusLine().toString() + "\n" + valueAsString);
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
}
