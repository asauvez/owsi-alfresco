package fr.openwide.alfresco.api.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class ThresholdBufferTest {
	
	@Test
	public void testMemory() throws IOException {
		ThresholdBufferFactory factory = ThresholdBufferFactory.newInstance(null, 10, Long.MAX_VALUE, false);
		try (ThresholdBuffer out = factory.newOutputStream()) {
			Assert.assertNull(out.tempFile);
			out.write("12345".getBytes());
			Assert.assertNull(out.tempFile);
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(out.newInputStream()))) {
				Assert.assertEquals("12345", reader.readLine());
			}
			Assert.assertNull(out.tempFile);
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(out.newInputStream()))) {
				Assert.assertEquals("12345", reader.readLine());
			}
		}
	}

	@Test
	public void testOverflowDisk() throws IOException {
		String value = "1234567890";
		
		File tempFolder = File.createTempFile("test", null);
		tempFolder.delete();
		tempFolder.mkdir();
		Assert.assertEquals(0, tempFolder.list().length);
		
		ThresholdBufferFactory factory = ThresholdBufferFactory.newInstance(tempFolder, 10, Long.MAX_VALUE, false);
		try (ThresholdBuffer out = factory.newOutputStream()) {
			Assert.assertNull(out.tempFile);
			for (int i=0; i<20_000; i++) {
				out.write(value.getBytes());
			}

			Assert.assertNotNull(out.tempFile);
			Assert.assertEquals(1, tempFolder.list().length);

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(out.newInputStream()))) {
				Assert.assertEquals(StringUtils.repeat(value, 20_000), reader.readLine());
			}
			Assert.assertNotNull(out.tempFile);
			
			// Relecture
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(out.newInputStream()))) {
				Assert.assertEquals(StringUtils.repeat(value, 20_000), reader.readLine());
			}

			Assert.assertEquals(1, tempFolder.list().length);
		}
		
		// Fichier effacé
		Assert.assertEquals(0, tempFolder.list().length);
	}
	
	@Test
	public void testMaxSize() throws IOException {
		ThresholdBufferFactory factory = ThresholdBufferFactory.newInstance(null, 10, 10, false);
		try (ThresholdBuffer out = factory.newOutputStream()) {
			Assert.assertNull(out.tempFile);
			out.write("1234567890123456".getBytes());
			Assert.assertTrue(false);
		} catch (IllegalStateException e) {}
	}
}
