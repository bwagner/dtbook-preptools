package ch.sbs.utils.preptools;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;


/**
	* Copyright (C) 2010 Swiss Library for the Blind, Visually Impaired and Print Disabled
	*
	* This file is part of dtbook-preptools.
	* 	
	* dtbook-preptools is free software: you can redistribute it
	* and/or modify it under the terms of the GNU Lesser General Public
	* License as published by the Free Software Foundation, either
	* version 3 of the License, or (at your option) any later version.
	* 	
	* This program is distributed in the hope that it will be useful,
	* but WITHOUT ANY WARRANTY; without even the implied warranty of
	* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
	* Lesser General Public License for more details.
	* 	
	* You should have received a copy of the GNU Lesser General Public
	* License along with this program. If not, see
	* <http://www.gnu.org/licenses/>.
	*/

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
