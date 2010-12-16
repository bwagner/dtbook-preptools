package ch.sbs.plugin.preptools;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.sbs.utils.preptools.vform.FileUtils;
import ch.sbs.utils.preptools.vform.PropsUtils;

public class PropsUtilsTest {
	private static final String PROPS_FILENAME = "stamp.properties";
	private static final String KEY = "stamp";
	private static final String VALUE = "15.12.2010 14:21:28";
	private final String filename = getClass().getProtectionDomain()
			.getCodeSource().getLocation().getFile().toString()
			+ "/" + PROPS_FILENAME;

	@Before
	public void setUp() throws IOException {
		final Properties props = new Properties();
		final File file = new File(filename);
		if (file.exists()) {
			FileUtils.delete(filename);
		}
		final FileOutputStream fos = new FileOutputStream(filename);
		props.put(KEY, VALUE);
		props.store(fos, "generated file");
		fos.close();

	}

	@After
	public void tearDown() {
		FileUtils.delete(filename);
	}

	@Test
	public void testPropsFromStartupDir() {
		final String contents = PropsUtils.loadFromStartupDir(getClass(),
				PROPS_FILENAME).getProperty(KEY);

		assertEquals(VALUE, contents);
	}
}
