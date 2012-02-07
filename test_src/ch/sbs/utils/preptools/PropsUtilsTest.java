package ch.sbs.utils.preptools;

import static org.junit.Assert.assertEquals;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

/**
 * Copyright (C) 2010 Swiss Library for the Blind, Visually Impaired and Print
 * Disabled
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
		final FileOutputStream fos = new FileOutputStream(filename);
		props.put(KEY, VALUE);
		props.store(fos, "generated file");
		fos.close();

	}

	@Test
	public void testPropsFromStartupDir() {
		final String contents = PropsUtils.loadFromStartupDir(getClass(),
				PROPS_FILENAME).getProperty(KEY);

		assertEquals(VALUE, contents);
	}
}
