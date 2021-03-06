package ch.sbs.utils.preptools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

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

public class PropsUtils {
	private PropsUtils() {
	}

	public static Properties loadFromStartupDir(final Class<?> clazz,
			final String filename) {
		final Properties props = new Properties();
		final String path = clazz.getProtectionDomain().getCodeSource()
				.getLocation().toString().substring("file:".length());
		final FileInputStream fis;
		try {
			fis = new FileInputStream(new java.io.File(path + "/" + filename));
		} catch (final FileNotFoundException e) {
			return props; // it's quite possible this properties file is not
							// around.
		}
		try {
			props.load(fis);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		try {
			fis.close();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		return props;
	}

	public static String get(final String key, final String filename) {
		Properties props;
		try {
			props = load(new File(filename));
		} catch (final IOException e) {
			return "getting key " + key + " from file " + filename + " failed "
					+ e;
		}

		return (props.getProperty(key));
		// return "'" + key + "' not found in " + filename;
	}

	/**
	 * Load a properties file from the classpath
	 * 
	 * @param filename
	 * @return Properties
	 * @throws IOException
	 */
	public static Properties load(final String filename) {
		final Properties props = new Properties();
		final URL url = ClassLoader.getSystemResource(filename);
		if (url != null) {
			try {
				props.load(url.openStream());
			} catch (final IOException e) {
				e.printStackTrace();
				props.put("stamp", "loading from url " + url + " for "
						+ filename + " failed " + e);
			}
		}
		else {
			props.put("stamp", "url was null for " + filename);
		}
		return props;
	}

	public static Properties loadForClass(final Class<?> clazz,
			final String filename) {
		final Properties props = new Properties();
		final InputStream in = clazz.getClassLoader().getResourceAsStream(
				filename);
		if (in != null) {
			try {
				props.load(in);
			} catch (IOException e) {
				e.printStackTrace();
				props.put("stamp", "loading from " + filename + "failed: " + e);
			}
		}
		else {
			props.put("stamp", "loading from " + filename + " failed!");

		}

		return props;
	}

	/**
	 * Load a Properties File
	 * 
	 * @param propsFile
	 * @return Properties
	 * @throws IOException
	 */
	public static Properties load(final File propsFile) throws IOException {
		final Properties props = new Properties();
		final FileInputStream fis = new FileInputStream(propsFile);
		props.load(fis);
		fis.close();
		return props;
	}
}
