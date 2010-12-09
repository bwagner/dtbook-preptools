package ch.sbs.utils.preptools.vform;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VFormUtil {

	// Using an array and not e.g. a Set, because the order of the patterns
	// is relevant since "Ihre" would possibly match before "Ihren" gets a
	// chance.
	private static final String[] vforms = new String[] { "Ihren", "Ihrem",
			"Ihres", "Ihre", "Ihr", "Ihnen", "Euer", "Eure", "Euren", "Eurem",
			"Eures", "Deinen", "Deinem", "Deines", "Deine", "Dein", "Sie",
			"Du", "Dir", "Dich", "Euch" };

	private static final Pattern vFormPattern;

	static {
		final StringBuilder bla = new StringBuilder();
		bla.append("(?:");
		for (final String vform : vforms) {
			bla.append(vform);// = Pattern.compile(vform + "\\b");
			bla.append("|");
		}
		bla.setLength(bla.length() - 1); // throw away last "|"
		bla.append(")\\b");
		vFormPattern = Pattern.compile(bla.toString());
		// vFormPattern = Pattern.compile(".*");
	}

	public static String replace(final String theText) {
		String result = theText;
		for (final String vform : vforms) {
			result = result.replaceAll(vform + "\\b",
					"<brl:v-form>$0</brl:v-form>");
		}
		return result;
	}

	// TODO: optimization potentials:
	// 1. reuse the matcher that has already matched text.
	public static Match find(final String text, int i) {
		final Matcher m = vFormPattern.matcher(text);
		if (m.find(i)) {
			return new Match(m.start(), m.end());
		} else {
			return NULL_MATCH;
		}
	}

	public static class Match {
		public Match(int start, int end) {
			startOffset = start;
			endOffset = end;
		}

		public int startOffset;
		public int endOffset;
	}

	public static final Match NULL_MATCH = new Match(-1, -1);
}
