/*
 *  Copyright 2013 Deutsche Nationalbibliothek
 *
 *  Licensed under the Apache License, Version 2.0 the "License";
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.culturegraph.mf.stream.source;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.culturegraph.mf.stream.DataFilePath;
import org.culturegraph.mf.stream.pipe.ObjectBuffer;
import org.culturegraph.mf.util.FileCompression;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests for file compression in {@link FileOpener}.
 * 
 * @author Christoph Böhme
 *
 */
@RunWith(Parameterized.class)
public final class FileOpenerCompressionTest {
	
	private static final String DATA = "This could have been a remarkable sentence.";
	
	// NO CHECKSTYLE VisibilityModifier FOR 3 LINES:
	// JUnit requires rules to be public
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	
	private final String resourcePath; 
	private final FileCompression compression; 
	
	public FileOpenerCompressionTest(final String resourcePath, final FileCompression compression) {
		this.resourcePath = resourcePath;
		this.compression = compression;
	}

	@Parameters
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] { 
				{ DataFilePath.COMPRESSED_NONE, FileCompression.AUTO },
				{ DataFilePath.COMPRESSED_BZ2, FileCompression.AUTO },
				{ DataFilePath.COMPRESSED_BZIP2, FileCompression.AUTO },
				{ DataFilePath.COMPRESSED_GZ, FileCompression.AUTO },
				{ DataFilePath.COMPRESSED_GZIP, FileCompression.AUTO },
				{ DataFilePath.COMPRESSED_XZ, FileCompression.AUTO },
				{ DataFilePath.COMPRESSED_NONE, FileCompression.NONE },
				{ DataFilePath.COMPRESSED_BZ2, FileCompression.BZIP2 },
				{ DataFilePath.COMPRESSED_BZIP2, FileCompression.BZIP2 },
				{ DataFilePath.COMPRESSED_GZ, FileCompression.GZIP },
				{ DataFilePath.COMPRESSED_GZIP, FileCompression.GZIP },
				{ DataFilePath.COMPRESSED_XZ, FileCompression.XZ },
			});
	}
	
	@Test
	public void testOpenCompressedFiles() throws IOException {
		final File file = tempFolder.newFile();

		final InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
		try {
			final OutputStream out = new FileOutputStream(file);
			try { IOUtils.copy(in, out); }
			finally { out.close(); }
		} finally { in.close(); }
		
		final FileOpener opener = new FileOpener();
		opener.setCompression(compression);
		final ObjectBuffer<Reader> buffer = new ObjectBuffer<Reader>();
		opener.setReceiver(buffer);	
		opener.process(file.getAbsolutePath());
		opener.closeStream();
		
		final Reader reader = buffer.pop();
		final String charsFromFile;
		try { charsFromFile = IOUtils.toString(reader); }
		finally { reader.close(); }
		
		assertEquals(DATA, charsFromFile);
	}
	
}
