package ch.sbs.utils.preptools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.sbs.utils.preptools.vform.MarkupUtil;

public class RegionSkipperLeaf implements RegionSkipperComponent {
	private final Pattern skipPattern;

	private List<Match> regionsToSkip;

	public RegionSkipperLeaf(final String pattern) {
		skipPattern = Pattern.compile(pattern);
	}

	@Override
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
	@Override
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

	/**
	 * Utility method to create a RegionSkipper for skipping text already marked
	 * up
	 * with the given tag.
	 * 
	 * @param tag
	 * @return RegionSkipper
	 */
	public static RegionSkipperComponent makeMarkupRegionSkipper(
			final String tag) {
		final StringBuilder sb = new StringBuilder();
		final String OPENING_TAG = "<" + tag + "\\s*>";
		final String NON_GREEDY_CONTENT = "(?s:.*?)";
		final String CLOSING_TAG = "</" + MarkupUtil.getClosingTag(tag)
				+ "\\s*>";
		sb.append(OPENING_TAG);
		sb.append(NON_GREEDY_CONTENT);
		sb.append(CLOSING_TAG);
		return new RegionSkipperLeaf(sb.toString());
	}

	public static RegionSkipperComponent getLiteralSkipper() {
		final String OPENING_TAG = "<brl:literal";
		final String OPTIONAL_ARG = "(?:\\s+brl:grade\\s*=\\s*\"\\d\")?";
		final String CLOSING_ANGLE = "\\s*>";
		final String NON_GREEDY_CONTENT = "(?s:.*?)";
		final String CLOSING_TAG = "</brl:literal\\s*>";
		final StringBuilder sb = new StringBuilder();
		sb.append(OPENING_TAG);
		sb.append(OPTIONAL_ARG);
		sb.append(CLOSING_ANGLE);
		sb.append(NON_GREEDY_CONTENT);
		sb.append(CLOSING_TAG);
		return new RegionSkipperLeaf(sb.toString());
	}

	public static RegionSkipperComponent getCommentSkipper() {
		return new RegionSkipperLeaf("(?s:<!--.*-->)");
	}

	/**
	 * Creates a RegionSkipperComposite for skipping literal sections
	 * and comments. Purposefully returns a RegionSkipperComposite instead
	 * of a RegionSkipperComponent, in order to enable clients to add their
	 * own skippers.
	 * 
	 * @return RegionSkipperComposite
	 */
	public static RegionSkipperComposite getLiteralAndCommentSkipper() {
		final RegionSkipperComposite literalAndComponentSkipper = new RegionSkipperComposite();
		literalAndComponentSkipper.addComponent(getLiteralSkipper());
		literalAndComponentSkipper.addComponent(getCommentSkipper());
		return literalAndComponentSkipper;
	}

}