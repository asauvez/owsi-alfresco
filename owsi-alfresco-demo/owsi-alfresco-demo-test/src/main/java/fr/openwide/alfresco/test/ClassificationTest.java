package fr.openwide.alfresco.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.junit.Test;

import fr.openwide.alfresco.test.client.AlfrescoRestClient;
import fr.openwide.alfresco.test.client.NodeRestClient;
import fr.openwide.alfresco.test.model.ExifProperties;
import fr.openwide.alfresco.test.model.NodeModelIT;
import fr.openwide.alfresco.test.model.PropertiesModelIT;

public class ClassificationTest {

	private AlfrescoRestClient alfrescoRestClient = new AlfrescoRestClient("demo");
	private NodeRestClient nodeRestClient = new NodeRestClient(alfrescoRestClient);
	
	@Test
	public void testClassificationSimple() {
		ExifProperties properties = new ExifProperties();
		properties.pixelXDimension = 640;
		properties.pixelYDimension = 480;
		NodeModelIT<ExifProperties> nodeToCreate = new NodeModelIT<ExifProperties>("toto", "cm:folder", properties);
		nodeToCreate.getAspectNames().add("owsi:classifiable");
		nodeRestClient.createNodeThenDelete(nodeToCreate, new Consumer<NodeModelIT<ExifProperties>>() {
			@Override
			public void accept(NodeModelIT<ExifProperties> nodeCreated) {
				NodeModelIT<PropertiesModelIT> node = nodeRestClient.getNode(nodeCreated.getId());
				nodeRestClient.assertPathValue(node, "/Company Home/Demo/exif/2019/640/480");
			}
		});
	}
	
	@Test
	public void testClassificationParallele() throws InterruptedException, ExecutionException {
		ExecutorService executorService = new ThreadPoolExecutor(1, 1, 1000, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>());

		List<Callable<String>> tasks = new ArrayList<Callable<String>>();
		for (int cpt = 0; cpt<1000; cpt++) {
			int finalCpt = cpt;
			tasks.add(new Callable<String>() {
				@Override
				public String call() throws Exception {
					return createOne(finalCpt);
				}
			});
		}
		
		List<Future<String>> futures = executorService.invokeAll(tasks);
		
		for (Future<String> future : futures) {
			nodeRestClient.deleteNode(future.get());
		}

		executorService.shutdown();
	}
	
	private String createOne(int cpt) {
		ExifProperties properties = new ExifProperties();
		properties.pixelXDimension = (int) (Math.random()*100);
		properties.pixelYDimension = (int) (Math.random()*100);
		NodeModelIT<ExifProperties> nodeToCreate = new NodeModelIT<ExifProperties>("toto_" + cpt, "cm:folder", properties);
		nodeToCreate.getAspectNames().add("owsi:classifiable");
		NodeModelIT<ExifProperties> createNode = nodeRestClient.createNode(nodeToCreate);
		return createNode.getId();
	}
}
