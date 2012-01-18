package ch.sbs.utils.string;

import java.util.Arrays;

public class StringUtils {

	/**
	 * Utility method to join an array of Strings.
	 * 
	 * @param strings
	 * @return concatenation of all strings with the delimiter in between
	 *         strings.
	 */
	public static String join(final String[] strings) {
		return Arrays.asList(strings).toString();
	}

}
