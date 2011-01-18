package ch.sbs.utils.preptools;

import java.io.File;
import java.net.URL;

public class FileUtils {

	public static void delete(final String fileName) {

		File f = new File(fileName);

		if (!f.exists())
			throw new IllegalArgumentException(
					"Delete: no such file or directory: " + fileName);

		if (!f.canWrite())
			throw new IllegalArgumentException("Delete: write protected: "
					+ fileName);

		// If it is a directory, make sure it is empty
		if (f.isDirectory()) {
			final String[] files = f.list();
			if (files.length > 0)
				throw new IllegalArgumentException(
						"Delete: directory not empty: " + fileName);
		}

		boolean success = f.delete();

		if (!success)
			throw new IllegalArgumentException("Delete: deletion failed");
	}

	public static String basename(final URL url) {
		return url == null ? "" : basename(url.getFile());
	}

	public static String basename(final String path) {

		return path == null ? "" : path.substring(path
				.lastIndexOf(File.separatorChar) + 1);
	}

}
