package ch.sbs.utils.preptools;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

import ch.sbs.utils.preptools.vform.FileUtils;

public class FileUtilsTest {

	@Test
	public void testBasenameUrl() throws MalformedURLException {
		final String basename = "Das_Herz_des_Urpferds.xml";
		final String url = "file:/home/wagnerb/projects/workspace/UTFX_run/resources/dtbook2sbsform/"
				+ basename;
		assertEquals(basename, FileUtils.basename(new URL(url)));
	}

	@Test
	public void testBasenameEmptyUrl() throws MalformedURLException {
		assertEquals("", FileUtils.basename((URL) null));
	}

	@Test
	public void testBasenameEmptyString() throws MalformedURLException {
		assertEquals("", FileUtils.basename((String) null));
	}

	@Test
	public void testBasenameString() throws MalformedURLException {
		final String basename = "Das_Herz_des_Urpferds.xml";
		final String url = "file:/home/wagnerb/projects/workspace/UTFX_run/resources/dtbook2sbsform/"
				+ basename;
		assertEquals(basename, FileUtils.basename(url));
	}
}
