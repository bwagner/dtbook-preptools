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
	public boolean inSkipRegion(final Matcher matcher) {
		boolean inSkipRegion = false;
		final Iterator<Match> it = regionsToSkip.iterator();
		while (!inSkipRegion && it.hasNext()) {
			final Match skipRegion = it.next();
			inSkipRegion = skipRegion.startOffset <= matcher.start()
					&& skipRegion.endOffset > matcher.start();
		}
		return inSkipRegion;
	}

}