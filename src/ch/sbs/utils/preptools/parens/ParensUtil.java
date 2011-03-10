package ch.sbs.utils.preptools.parens;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.sbs.utils.preptools.Match;
import ch.sbs.utils.preptools.RegionSkipper;

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

		/**
		 * Finds list of potentially orphaned parens.
		 * 
		 * @param theText
		 *            The text to search
		 * @param offset
		 *            from where to start searching in the given text.
		 * @param theRegionSkipper
		 * @return list of potentially orphaned parens. It can be empty.
		 */
		public List<Match> findOrphans(final String theText, int offset,
				final RegionSkipper theRegionSkipper) {
			theRegionSkipper.findRegionsToSkip(theText);
			final List<Match> orphans = new ArrayList<Match>();
			final String[][] patternPairs = getPatternPairs();
			for (final String[] patternPair : patternPairs) {
				final Pattern pattern = getPairPattern(patternPair);
				final Matcher matcher = pattern.matcher(theText);

				// must be called before closingChar!
				final char openingChar = getOpeningChar(matcher, patternPair);

				final char closingChar = getClosingChar(matcher, patternPair);
				boolean expectOpening = true;
				Match match = null;
				Match previousMatch = null;
				matcher.reset();
				while (matcher.find()) {
					if (theRegionSkipper.inSkipRegion(matcher)
							|| matcher.start() < offset) {
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

		private Pattern getPairPattern(final String[] patPair) {
			final StringBuilder sb = new StringBuilder();
			sb.append(escape(patPair[0]));
			sb.append("|");
			sb.append(escape(patPair[1]));
			final Pattern pattern = Pattern.compile(sb.toString());
			return pattern;
		}

		/**
		 * Hook to provide the opening character.
		 * 
		 * @param matcher
		 * @param patPair
		 * @return
		 */
		protected abstract char getOpeningChar(final Matcher matcher,
				final String[] patPair);

		/**
		 * Hook to provide the closing character.
		 * 
		 * @param matcher
		 * @param patPair
		 * @return
		 */
		protected abstract char getClosingChar(final Matcher matcher,
				final String[] patPair);

		protected abstract String[][] getPatternPairs();

		/**
		 * Optional hook to escape the given String
		 * 
		 * @param theString
		 * @return escaped string
		 */
		protected String escape(final String theString) {
			return theString;
		}
	}

	static class QuoteOrphanMatcher extends OrphanMatcher {

		private static final char UNSET_CHAR = 0;
		static final String[] AQUO_PAIR = new String[] { "«", "»" };
		static final String[] SAQUO_PAIR = new String[] { "‹", "›" };
		static final String[] ANG_PAIR = new String[] { "〈", "〉" };
		static final String[][] PAIRS = new String[][] { AQUO_PAIR, SAQUO_PAIR,
				ANG_PAIR, };
		private final Map<String[], Character> openingChar;
		private final Map<String[], Character> closingChar;

		public QuoteOrphanMatcher() {
			openingChar = new HashMap<String[], Character>();
			closingChar = new HashMap<String[], Character>();
		}

		@Override
		protected String[][] getPatternPairs() {
			return PAIRS;
		}

		@Override
		protected char getOpeningChar(final Matcher matcher,
				final String[] patPair) {
			if (openingChar.containsKey(patPair)) {
				return openingChar.get(patPair);
			}
			if (!matcher.find()) {
				return UNSET_CHAR;
			}
			final char foundOpeningChar = matcher.group().charAt(0);
			openingChar.put(patPair, foundOpeningChar);
			closingChar.put(patPair,
					patPair[patPair[0].charAt(0) == foundOpeningChar ? 1 : 0]
							.charAt(0));
			return foundOpeningChar;
		}

		@Override
		protected char getClosingChar(final Matcher matcher,
				final String[] patPair) {
			final Character character = closingChar.get(patPair);
			return character == null ? 0 : character;
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
		protected String escape(final String theString) {
			return "\\" + theString;
		}
	}

	/**
	 * Finds list of potentially orphaned parens.
	 * 
	 * @param theText
	 *            The text to search
	 * @param offset
	 *            from where to start searching in the given text.
	 * @param theRegionSkipperComponent
	 * @return list of potentially orphaned parens. It can be empty.
	 */
	public static List<Match> findOrphans(final String theText, int offset,
			final RegionSkipper theRegionSkipperComponent) {
		final List<Match> orphans = new QuoteOrphanMatcher().findOrphans(
				theText, offset, theRegionSkipperComponent);
		orphans.addAll(new ParensOrphanMatcher().findOrphans(theText, offset,
				theRegionSkipperComponent));
		Collections.sort(orphans);
		return orphans;
	}

	/**
	 * Finds list of potentially orphaned parens.
	 * 
	 * @param theText
	 *            The text to search
	 * @param theRegionSkipperComponent
	 * @return list of potentially orphaned parens. It can be empty.
	 */
	public static List<Match> findOrphans(final String theText,
			final RegionSkipper theRegionSkipperComponent) {
		return findOrphans(theText, 0, theRegionSkipperComponent);
	}
}
