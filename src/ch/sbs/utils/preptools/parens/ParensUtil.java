package ch.sbs.utils.preptools.parens;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.sbs.utils.preptools.Match;

public class ParensUtil {

	public static final char LAQUO = '«';
	public static final char RAQUO = '»';
	public static final char LSAQUO = '‹';
	public static final char RSAQUO = '›';
	public static final char LANG = '〈';
	public static final char RANG = '〉';
	public static final char LBRACE = '{';
	public static final char RBRACE = '}';
	public static final char LBRACKET = '[';
	public static final char RBRACKET = ']';
	public static final char LPAREN = '(';
	public static final char RPAREN = ')';

	static abstract class OrphanMatcher {

		private static final Pattern skipPattern;

		static {
			// http://download.oracle.com/javase/tutorial/essential/regex/quant.html
			final String OPENING_TAG = "<\\s*brl\\s*:\\s*literal";
			final String OPTIONAL_ARG = "(?:\\s+brl\\s*:grade\\s*=\\s*\"\\d\")?";
			final String CLOSING_ANGLE = ">";
			final String NON_GREEDY_CONTENT = ".*?";
			final String CLOSING_TAG = "<\\s*/\\s*brl\\s*:\\s*literal\\s*>";
			final StringBuilder sb = new StringBuilder();
			sb.append(OPENING_TAG);
			sb.append(OPTIONAL_ARG);
			sb.append(CLOSING_ANGLE);
			sb.append(NON_GREEDY_CONTENT);
			sb.append(CLOSING_TAG);
			skipPattern = Pattern.compile(sb.toString());
		}

		private final List<Match> regionsToSkip;

		private void findRegionsToSkip(final String theText) {
			final Matcher matcher = skipPattern.matcher(theText);
			while (matcher.find()) {
				regionsToSkip.add(new Match(matcher.start(), matcher.end()));
			}
		}

		private boolean inSkipRegion(final Matcher matcher) {
			boolean inSkipRegion = false;
			final Iterator<Match> it = regionsToSkip.iterator();
			while (!inSkipRegion && it.hasNext()) {
				final Match skipRegion = it.next();
				inSkipRegion = skipRegion.startOffset <= matcher.start()
						&& skipRegion.endOffset > matcher.start();
			}
			return inSkipRegion;
		}

		public OrphanMatcher() {
			regionsToSkip = new ArrayList<Match>();
		}

		public List<Match> findOrphans(final String theText) {
			findRegionsToSkip(theText);
			final List<Match> orphans = new ArrayList<Match>();
			final String[][] patternPairs = getPatternPairs();
			for (final String[] patPair : patternPairs) {
				final StringBuilder sb = new StringBuilder();
				sb.append(escape(patPair[0]));
				sb.append("|");
				sb.append(escape(patPair[1]));
				final Pattern pattern = Pattern.compile(sb.toString());
				final Matcher matcher = pattern.matcher(theText);

				// must be called before closingChar!
				final char openingChar = getOpeningChar(matcher, patPair);

				final char closingChar = getClosingChar(matcher, patPair);
				boolean expectOpening = true;
				Match match = null;
				Match previousMatch = null;
				matcher.reset();
				while (matcher.find()) {
					if (inSkipRegion(matcher)) {
						continue;
					}
					final char matchChar = matcher.group().charAt(0);
					previousMatch = match;
					match = new Match(matcher.start(), matcher.end());
					if (expectOpening) {
						if (matchChar == closingChar) {
							// error. Since it's closing brace we leave
							// expectation to opening next. expectOpening ==
							// true (no change)
							orphans.add(match);
						}
						else {
							expectOpening = false;
						}
					}
					else {
						if (matchChar == openingChar) {
							// error. Since it's opening brace we leave
							// expectation to closing next. expectOpening ==
							// false (no change) We point to the previous match
							// that is the brace with the missing closing brace.
							orphans.add(previousMatch);
						}
						else {
							expectOpening = true;
						}
					}
				}
				if (!expectOpening) {
					orphans.add(match);
				}
			}
			return orphans;
		}

		protected abstract char getOpeningChar(final Matcher matcher,
				final String[] patPair);

		protected abstract char getClosingChar(final Matcher matcher,
				final String[] patPair);

		protected abstract String[][] getPatternPairs();

		protected String escape(final String str) {
			return str;
		}
	}

	static class QuoteOrphanMatcher extends OrphanMatcher {

		private static final char UNSET_CHAR = 0;
		private char openingChar = UNSET_CHAR;
		private char closingChar = UNSET_CHAR;

		@Override
		protected String[][] getPatternPairs() {
			return new String[][] { { "«", "»" }, { "‹", "›" }, { "〈", "〉" }, };
		}

		@Override
		protected char getOpeningChar(final Matcher matcher,
				final String[] patPair) {
			if (openingChar != UNSET_CHAR) {
				return openingChar;
			}
			if (!matcher.find()) {
				return openingChar;
			}
			openingChar = matcher.group().charAt(0);
			closingChar = patPair[patPair[0].charAt(0) == openingChar ? 1 : 0]
					.charAt(0);
			return openingChar;
		}

		@Override
		protected char getClosingChar(final Matcher matcher,
				final String[] patPair) {
			return closingChar;
		}
	}

	static class ParensOrphanMatcher extends OrphanMatcher {

		@Override
		protected String[][] getPatternPairs() {
			return new String[][] { { "{", "}" }, { "[", "]" }, { "(", ")" }, };
		}

		@Override
		protected char getOpeningChar(final Matcher matcher,
				final String[] patPair) {
			return patPair[0].charAt(0);
		}

		@Override
		protected char getClosingChar(final Matcher matcher,
				final String[] patPair) {
			return patPair[1].charAt(0);
		}

		@Override
		protected String escape(final String str) {
			return "\\" + str;
		}
	}

	static List<Match> findOrphansQuotes(final String theText) {
		final List<Match> findOrphans = new QuoteOrphanMatcher()
				.findOrphans(theText);
		return findOrphans;
	}

	static List<Match> findOrphansParens(final String theText) {
		final List<Match> findOrphans = new ParensOrphanMatcher()
				.findOrphans(theText);
		return findOrphans;
	}

	/**
	 * Finds list of potentially orphaned parens.
	 * 
	 * @param theText
	 *            The text to search
	 * @return list of potentially orphaned parens. It can be empty.
	 */
	public static List<Match> findOrphans(final String theText) {
		final List<Match> orphans = new QuoteOrphanMatcher()
				.findOrphans(theText);
		orphans.addAll(new ParensOrphanMatcher().findOrphans(theText));
		Collections.sort(orphans);
		return orphans;
	}
}
