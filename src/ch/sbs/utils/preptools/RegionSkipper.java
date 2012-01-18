package ch.sbs.utils.preptools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.sbs.utils.intervaltree.Interval;
import ch.sbs.utils.intervaltree.IntervalTree;

/**
 * Allows to ignore regions in a text when searching for patterns.
 * The regions to skip are specified via regexes.
 * 
 * Usage:
 * 1. Setting up:
 * 1.1. create a RegionSkipper
 * 1.2. (optional) add patterns
 * 1.3. call findRegionsToSkip for a given text
 * 2. Using:
 * Call inSkipRegion(Matcher)
 */
public class RegionSkipper {
	private final IntervalTree tree;
	private String skipPattern;

	public RegionSkipper(final String pattern) {
		skipPattern = pattern;
		tree = new IntervalTree();
	}

	public RegionSkipper addPattern(final String pattern) {
		if (skipPattern != null && skipPattern.length() > 0) {
			skipPattern += "|" + pattern;
		}
		else {
			skipPattern = pattern;
		}
		return this;
	}

	// java.util.regex.Matcher's definition of a region includes the lower bound
	// but excludes the upper, while the IntervalTree includes both bounds.
	// This means we have to subtract 1 from the upper bound.
	public void addRegion(int start, int end) {
		tree.insert(new Interval(start, end - 1));
	}

	public void findRegionsToSkip(final String theText) {
		final Matcher matcher = Pattern.compile(skipPattern).matcher(theText);
		while (matcher.find()) {
			addRegion(matcher.start(), matcher.end());
		}
	}

	// java.util.regex.Matcher's definition of a region includes the lower bound
	// but excludes the upper, while the IntervalTree includes both bounds.
	// This means we have to subtract 1 from the upper bound.
	/**
	 * Indicates whether a given matcher's last match lies in a region to
	 * ignore.
	 * 
	 * @param matcher
	 *            The matcher for which to check the last match.
	 * @return True if the last match for the given matcher lies in a region to
	 *         ignore.
	 */
	public boolean inSkipRegion(final Matcher matcher) {
		if (tree == null) {
			throw new RuntimeException(
					"You need to call findRegionsToSkip first!");
		}
		return tree.search(new Interval(matcher.start(), matcher.end() - 1)) != null;
	}

	/**
	 * Factory method to create a RegionSkipper for a given tag.
	 * 
	 * @param tag
	 * @return RegionSkipper
	 */
	public static RegionSkipper makeMarkupRegionSkipper(final String tag) {
		return new RegionSkipper(makeMarkupRegex(tag));
	}

	/**
	 * Creates the regex for an xml element with given tag.
	 * 
	 * @param tag
	 *            the tag for which to create an element regex.
	 * @return the regex for the element for the given tag.
	 */
	public static String makeMarkupRegex(final String tag) {
		final StringBuilder sb = new StringBuilder();
		final String OPENING_TAG = "<" + tag + "\\s*>";
		final String NON_GREEDY_CONTENT = "(?s:.*?)";
		final String CLOSING_TAG = "</" + MarkupUtil.getClosingTag(tag)
				+ "\\s*>";
		sb.append(OPENING_TAG);
		sb.append(NON_GREEDY_CONTENT);
		sb.append(CLOSING_TAG);
		return sb.toString();
	}

	/**
	 * Creates a RegionSkipper for literal braille.
	 * 
	 * @return
	 */
	public static RegionSkipper getLiteralSkipper() {
		return new RegionSkipper(makeLiteralRegex());
	}

	static String makeLiteralRegex() {
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
		return sb.toString();
	}

	/**
	 * Creates a regex for the opening region of the dtbook xml
	 * up to the opening book tag.
	 * 
	 * @return The regex for the opening region.
	 */
	static String makeHeaderRegex() {
		return "(?s:^.*<book)";
	}

	/**
	 * Creates a skipper for comments.
	 * 
	 * @return
	 */
	public static RegionSkipper getCommentSkipper() {
		return new RegionSkipper(makeCommentRegex());
	}

	/**
	 * Creates a regex for skipping xml comments.
	 * 
	 * @return regex for skipping xml comments.
	 */
	static String makeCommentRegex() {
		return "(?s:<!--(?:.*?)-->)";
	}

	/**
	 * Creates a RegionSkipper for skipping literal sections, comments, and
	 * everything up to the opening book tag.
	 * 
	 * @return RegionSkipper
	 */
	public static RegionSkipper getDefaultSkipper() {
		final RegionSkipper skipper = new RegionSkipper(makeLiteralRegex());
		skipper.addPattern(makeCommentRegex()).addPattern(makeHeaderRegex());
		return skipper;
	}
}
