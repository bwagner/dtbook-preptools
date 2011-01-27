package ch.sbs.utils.preptools.vform;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.sbs.utils.preptools.Match;

public class VFormUtil {
	static final String ELEMENT_NAME = "brl:v-form";

	// Mail von Mischa Kuenzle 12.1.2011 15:09
	// 3. Person PL (obligatorische Abfrage)
	private static final String[] thirdPP = new String[] { "Ihnen", "Ihr",
			"Ihre", "Ihrem", "Ihren", "Ihrer", "Ihrerseits", "Ihres",
			"Ihresgleichen", "Ihrethalben", "Ihretwegen", "Ihretwillen",
			"Ihrige", "Ihrigem", "Ihrigen", "Ihriger", "Ihriges", "Ihrs",
			"Sie", };

	// Mail von Mischa Kuenzle 12.1.2011 15:09
	// 2. Person (optionale Abfrage)
	private static final String[] secondP = new String[] { "Dein", "Deine",
			"Deinem", "Deinen", "Deiner", "Deinerseits", "Deines",
			"Deinesgleichen", "Deinethalben", "Deinetwegen", "Deinetwillen",
			"Deinige", "Deinigem", "Deinigen", "Deiniger", "Deiniges", "Deins",
			"Dich", "Dir", "Du", "Euch", "Euer", "Euere", "Euerem", "Euerer",
			"Eueres", "Euers", "Euerseits", "Eure", "Eurem", "Euren",
			"Eurerseits", "Eures", "Euresgleichen", "Eurethalben",
			"Euretwegen", "Euretwillen", "Eurige", "Eurigem", "Eurigen",
			"Euriger", "Euriges", };

	private static final String[] forms3rdPersonPlural = new String[] { WordHierarchyBuilder
			.createWordTree(thirdPP).toRegex() };

	private static final String[] forms2ndPerson = new String[] { WordHierarchyBuilder
			.createWordTree(secondP).toRegex() };

	private static final String[] allForms;

	static {
		allForms = new String[forms3rdPersonPlural.length
				+ forms2ndPerson.length];
		int i = 0;
		for (final String form : forms3rdPersonPlural) {
			allForms[i++] = form;
		}
		for (final String form : forms2ndPerson) {
			allForms[i++] = form;
		}
	}

	private static Pattern vFormDefaultPattern;
	private static final Pattern vFormPatternAll;
	private static final Pattern vFormPattern3rdPersonPlural;

	static {
		vFormPatternAll = makePattern(allForms);
		vFormPattern3rdPersonPlural = makePattern(forms3rdPersonPlural);
		vFormDefaultPattern = vFormPatternAll;
	}

	public static Pattern get3rdPPPattern() {
		return vFormPattern3rdPersonPlural;
	}

	public static Pattern getAllPattern() {
		return vFormPatternAll;
	}

	protected static Pattern makePattern(final String[] forms) {
		final StringBuffer sb = new StringBuffer("(?:"); // non-capturing group,
															// see
		// http://download.oracle.com/javase/1.5.0/docs/api/java/util/regex/Pattern.html#special
		for (final String form : forms) {
			sb.append("(?<!<" + ELEMENT_NAME + ">)"); // negative lookbehind
			sb.append(form);
			sb.append("(?!</" + ELEMENT_NAME + ">)"); // negative lookahead
			sb.append("|");
		}
		sb.setLength(sb.length() - 1); // chop off last "|"
		sb.append(")\\b"); // make sure we don't match substrings.
		return Pattern.compile(sb.toString());
	}

	/**
	 * Returns the given text with matching parts surrounded by
	 * <brl:v-form></brl:v-form>
	 * 
	 * @param theText
	 *            the text to work on
	 * @param pattern
	 *            the pattern to match
	 * @return the given text with matching parts surrounded by
	 *         <brl:v-form></brl:v-form>
	 */
	public static String replace(final String theText, final Pattern pattern) {
		return pattern.matcher(theText).replaceAll(wrap("$0"));
		// Group zero always stands for the entire expression.
		// http://download.oracle.com/javase/1.5.0/docs/api/index.html?java/util/regex/Matcher.html
	}

	/**
	 * Does the same as @see replace using the default pattern.
	 * 
	 * @param theText
	 * @return
	 */
	public static String replace(final String theText) {
		return replace(theText, vFormDefaultPattern);
	}

	/**
	 * Returns Match where the pattern occurs or NULL_MATCH
	 * 
	 * @param text
	 *            Text to search
	 * @param start
	 *            index where to start
	 * @param pattern
	 *            pattern to match
	 * @return Match
	 */
	public static Match find(final String text, int start, final Pattern pattern) {
		final Matcher m = pattern.matcher(text);
		return m.find(start) ? new Match(m.start(), m.end()) : Match.NULL_MATCH;
	}

	/**
	 * Does the same as @see find using the default pattern.
	 * 
	 * @param text
	 * @param start
	 * @return
	 */
	public static Match find(final String text, int start) {
		return find(text, start, vFormDefaultPattern);
	}

	/**
	 * Indicates whether text matches pattern
	 * 
	 * @param text
	 * @param pattern
	 * @return
	 */
	public static boolean matches(final String text, final Pattern pattern) {
		return text != null && pattern.matcher(text).matches();
	}

	/**
	 * Does the same as matches for the default pattern
	 * 
	 * @param text
	 * @return
	 */
	public static boolean matches(final String text) {
		return matches(text, vFormDefaultPattern);
	}

	public static final String wrap(final String theString) {
		return wrap(theString, ELEMENT_NAME);
	}

	public static final String wrap(final String theString,
			final String theElement) {
		final StringBuilder sb = new StringBuilder("<");
		sb.append(theElement);
		sb.append(">");
		sb.append(theString);
		sb.append("</");
		sb.append(theElement);
		sb.append(">");
		return sb.toString();
	}
}
