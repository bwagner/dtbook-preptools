package ch.sbs.utils.preptools.vform;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VFormUtil {

	private static final String[] forms = new String[] { "Sie", "Ihrethalber",
			"Ihretwegen", "Ihren", "Ihrem", "Ihres", "Ihre", "Ihr", "Ihnen",
			"Du", "Dich", "Dir", "Deinethalber", "Deinetwegen", "Deinen",
			"Deinem", "Deines", "Deine", "Dein", "Eurethalber", "Euretwegen",
			"Euren", "Eurem", "Eures", "Eure", "Euer", "Euch" };

	private static final Pattern vFormPattern;

	static {

		final StringBuffer sb = new StringBuffer("(?:"); // non-capturing group,
															// see
		// http://download.oracle.com/javase/1.5.0/docs/api/java/util/regex/Pattern.html#special
		for (final String form : forms) {
			sb.append(form);
			sb.append("|");
		}
		sb.setLength(sb.length() - 1); // chop off last "|"
		sb.append(")\\b"); // make sure we don't match substrings.
		vFormPattern = Pattern.compile(sb.toString());
	}

	public static String replace(final String theText) {
		return vFormPattern.matcher(theText).replaceAll(
				"<brl:v-form>$0</brl:v-form>");
		// Group zero always stands for the entire expression.
		// http://download.oracle.com/javase/1.5.0/docs/api/index.html?java/util/regex/Matcher.html
	}

	public static Match find(final String text, int i) {
		final Matcher m = vFormPattern.matcher(text);
		return m.find(i) ? new Match(m.start(), m.end()) : NULL_MATCH;
	}

	public static boolean matches(final String text) {
		return text != null && vFormPattern.matcher(text).matches();
	}

	public static class Match {
		public Match(int start, int end) {
			startOffset = start;
			endOffset = end;
		}

		public int startOffset;
		public int endOffset;
	}

	public static final String wrap(final String theString) {
		final String element = "brl:v-form";
		final StringBuilder sb = new StringBuilder("<");
		sb.append(element);
		sb.append(">");
		sb.append(theString);
		sb.append("</");
		sb.append(element);
		sb.append(">");
		return sb.toString();
	}

	public static final Match NULL_MATCH = new Match(-1, -1);
}
