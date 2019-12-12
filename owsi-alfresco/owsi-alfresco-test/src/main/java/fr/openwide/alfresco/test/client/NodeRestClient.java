package fr.openwide.alfresco.test.client;

import java.util.function.Consumer;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.junit.Assert;

import com.fasterxml.jackson.databind.JavaType;

import fr.openwide.alfresco.test.model.NodeEntryModelIT;
import fr.openwide.alfresco.test.model.NodeModelIT;
import fr.openwide.alfresco.test.model.PropertiesModelIT;

public class NodeRestClient {

	private final AlfrescoRestClient client;
	
	public NodeRestClient(AlfrescoRestClient client) {
		this.client = client;
	}
	
	public <P extends PropertiesModelIT> NodeModelIT<P> createNode(NodeModelIT<P> objectJson) {
		String url = "/api/-default-/public/alfresco/versions/1/nodes/-my-/children?include=path,permissions";
		HttpPost request = client.postRequest(url, objectJson);
		NodeEntryModelIT<P> entry = client.request(request, getNodeEntryModelITClass(objectJson.getProperties().getClass()));
		return entry.getEntry();
	}
	
	public <P extends PropertiesModelIT> void createNodeThenDelete(NodeModelIT<P> objectJson, Consumer<NodeModelIT<P>> consumer) {
		NodeModelIT<P> node = createNode(objectJson);
		try {
			consumer.accept(node);
		} finally {
			deleteNode(node.getId());
		}
	}
	
	public NodeModelIT<PropertiesModelIT> getNode(String id) {
		return getNode(id, PropertiesModelIT.class);
	}
	public <P extends PropertiesModelIT> NodeModelIT<P> getNode(String id, Class<P> propertiesClass) {
		String url = "/api/-default-/public/alfresco/versions/1/nodes/" + id + "?include=path,permissions";
		HttpGet request = client.getRequest(url);
		NodeEntryModelIT<P> entry = client.request(request, getNodeEntryModelITClass(propertiesClass));
		return entry.getEntry();
	}
	public byte[] getContent(String id) {
		String url = "/api/-default-/public/alfresco/versions/1/nodes/" + id + "/content";
		HttpGet request = client.getRequest(url);
		return client.request(request, byte[].class);
	}
	
	public <P extends PropertiesModelIT> NodeEntryModelIT<P> putNode(String id, NodeModelIT<P> objectJson) {
		String url = "/api/-default-/public/alfresco/versions/1/nodes/" + id;
		HttpPut request = client.putRequest(url, objectJson);
		return client.request(request, getNodeEntryModelITClass(objectJson.getProperties().getClass()));
	}
	
	public void deleteNode(String id) {
		String urlDelete = "/api/-default-/public/alfresco/versions/1/nodes/" + id + "?permanent=false";
		client.delete(urlDelete);
	}

	public void assertPathValue(NodeModelIT<?> node, String path) {
		Assert.assertEquals(path, node.getPath().getName().replaceFirst("/Espace racine", "/Company Home"));
	}
	
	private JavaType getNodeEntryModelITClass(Class<?> propertiesClass) {
		return client.getMapper().getTypeFactory().constructParametricType(NodeEntryModelIT.class, propertiesClass);
	}
}
