package ch.sbs.utils.preptools;

public class TextUtils {

	/**
	 * Encloses the input string into protective characters \Q und \E, thus
	 * disabling special meaning of regex meta chars like *, ?, ., etc.
	 * 
	 * @see <a
	 *      href="http://download.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html#quot">quot</a>
	 * 
	 * @param input
	 *            string to quote
	 * @return quoted input
	 */
	public static String quoteRegexMeta(final String input) {
		return "\\Q" + input + "\\E";
	}

	/**
	 * Encloses the input string thus making it a non-capturing group with case
	 * folding (ignored case).
	 * 
	 * @see <a
	 *      href="http://download.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html#CASE_INSENSITIVE">CASE_INSENSITIVE</a>
	 * 
	 * @param input
	 *            string to enable case folding on.
	 * @return case folding enabled string.
	 */
	public static String wrapI(final String input) {
		return "(?i:" + input + ")";
	}

	/**
	 * Encloses the input string thus making it a non-capturing group with
	 * unicode-aware case folding (ignored case).
	 * 
	 * * @see <a href=
	 * "http://download.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html#UNICODE_CASE"
	 * >UNICODE_CASE</a>
	 * 
	 * 
	 * @param input
	 *            string to enable case folding on.
	 * @return case folding enabled string.
	 */
	public static String wrapIU(final String input) {
		return "(?u:" + wrapI(input) + ")";
	}

	/**
	 * Encloses the input string thus making it a non-capturing group where a
	 * dot matches any character including a line terminator.
	 * 
	 * @see <a
	 *      href="http://download.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html#DOTALL">DOTALL</a>
	 * 
	 * @param input
	 *            string to enable case folding on.
	 * @return case folding enabled string.
	 */
	public static String wrapDotAll(final String input) {
		return "(?s:" + input + ")";
	}

	/**
	 * Encloses the input string thus making it a non-capturing group with
	 * multiline flag enabled.
	 * In multiline mode the expressions ^ and $ match just after or just
	 * before, respectively, a line terminator or the end of the input sequence.
	 * By default these expressions only match at the beginning and the end of
	 * the entire input sequence.
	 * 
	 * @see <a
	 *      href="http://download.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html#MULTILINE">MULTILINE</a>
	 * 
	 * @param input
	 *            string to enable case folding on.
	 * @return case folding enabled string.
	 */
	public static String wrapMultiline(final String input) {
		return "(?m:" + input + ")";
	}

	/**
	 * Encloses the input string thus making it a non-capturing group with
	 * comments flag enabled.
	 * 
	 * Permits whitespace and comments in pattern.
	 * In this mode, whitespace is ignored, and embedded comments starting with
	 * # are ignored until the end of a line.
	 * 
	 * @see <a
	 *      href="http://download.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html#COMMENTS">COMMENTS</a>
	 * 
	 * @param input
	 *            string to enable case folding on.
	 * @return case folding enabled string.
	 */
	public static String wrapComments(final String input) {
		return "(?x:" + input + ")";
	}

}
