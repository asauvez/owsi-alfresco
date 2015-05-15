/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.openwide.alfresco.api.core.node.binding;

import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Extrait de la classe {@link org.springframework.util.StreamUtils}, uniquement disponible en Spring 4, donc pas sur Alfresco.
 */
public final class NonClosingStreamUtils {

	/**
	 * Returns a variant of the given {@link InputStream} where calling
	 * {@link InputStream#close() close()} has no effect.
	 * @param in the InputStream to decorate
	 * @return a version of the InputStream that ignores calls to close
	 */
	public static InputStream nonClosing(InputStream in) {
		return new NonClosingInputStream(in);
	}

	/**
	 * Returns a variant of the given {@link OutputStream} where calling
	 * {@link OutputStream#close() close()} has no effect.
	 * @param out the OutputStream to decorate
	 * @return a version of the OutputStream that ignores calls to close
	 */
	public static OutputStream nonClosing(OutputStream out) {
		return new NonClosingOutputStream(out);
	}


	private static class NonClosingInputStream extends FilterInputStream {
		
		public NonClosingInputStream(InputStream in) {
			super(in);
		}
		
		@Override
		public void close() throws IOException {
		}
	}

	private static class NonClosingOutputStream extends FilterOutputStream {
		
		public NonClosingOutputStream(OutputStream out) {
			super(out);
		}
		
		@Override
		public void write(byte[] b, int off, int let) throws IOException {
			// It is critical that we override this method for performance
			out.write(b, off, let);
		}
		
		@Override
		public void close() throws IOException {
		}
	}

	private NonClosingStreamUtils() {}

}
