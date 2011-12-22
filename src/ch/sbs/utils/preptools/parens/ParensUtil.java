package ch.sbs.utils.preptools.parens;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.sbs.utils.preptools.Match;
import ch.sbs.utils.preptools.RegionSkipper;

public class ParensUtil {

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
	public static List<Match> findOrphans(final String theText, int offset,
			final RegionSkipper theRegionSkipper) {
		theRegionSkipper.findRegionsToSkip(theText);
		final List<Match> orphans = new ArrayList<Match>();
		final String[][] patternPairs = new String[][] { { "{", "}" },
				{ "[", "]" }, { "(", ")" }, { "»", "«" }, { "›", "‹" }, };
		for (final String[] patternPair : patternPairs) {
			final Pattern pattern = getPairPattern(patternPair);
			final Matcher matcher = pattern.matcher(theText);

			final char openingChar = patternPair[0].charAt(0);

			final char closingChar = patternPair[1].charAt(0);
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
		Collections.sort(orphans);
		return orphans;
	}

	private static Pattern getPairPattern(final String[] patPair) {
		final StringBuilder sb = new StringBuilder();
		sb.append(escape(patPair[0]));
		sb.append("|");
		sb.append(escape(patPair[1]));
		final Pattern pattern = Pattern.compile(sb.toString());
		return pattern;
	}

	private static String escape(final String theString) {
		return "\\" + theString;
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
