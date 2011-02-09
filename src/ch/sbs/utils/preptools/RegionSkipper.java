package ch.sbs.utils.preptools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegionSkipper {
	private final Pattern skipPattern;

	private List<Match> regionsToSkip;

	public RegionSkipper(final String pattern) {
		skipPattern = Pattern.compile(pattern);
	}

	public void findRegionsToSkip(final String theText) {
		regionsToSkip = new ArrayList<Match>();
		final Matcher matcher = skipPattern.matcher(theText);
		while (matcher.find()) {
			regionsToSkip.add(new Match(matcher.start(), matcher.end()));
		}
	}

	// TODO: This could be optimized by a binary search.
	// The regionsToSkip are sorted anyway.
	/**
	 * @param matcher
	 * @return
	 */
	public boolean inSkipRegion(final Matcher matcher) {
		if (regionsToSkip == null) {
			throw new RuntimeException(
					"You need to call findRegionsToSkip first!");
		}
		boolean inSkipRegion = false;
		final Iterator<Match> it = regionsToSkip.iterator();
		while (!inSkipRegion && it.hasNext()) {
			final Match skipRegion = it.next();
			inSkipRegion = skipRegion.startOffset <= matcher.start()
					&& skipRegion.endOffset > matcher.start();
		}
		return inSkipRegion;
	}

	private static final RegionSkipper literalSkipper;
	static {
		final String OPENING_TAG = "<brl:literal";
		final String OPTIONAL_ARG = "(?:\\s+brl:grade\\s*=\\s*\"\\d\")?";
		final String CLOSING_ANGLE = "\\s*>";
		final String NON_GREEDY_CONTENT = ".*?";
		final String CLOSING_TAG = "</brl:literal\\s*>";
		final StringBuilder sb = new StringBuilder();
		sb.append(OPENING_TAG);
		sb.append(OPTIONAL_ARG);
		sb.append(CLOSING_ANGLE);
		sb.append(NON_GREEDY_CONTENT);
		sb.append(CLOSING_TAG);
		literalSkipper = new RegionSkipper(sb.toString());
	}

	public static RegionSkipper getLiteralSkipper() {
		return literalSkipper;
	}

}