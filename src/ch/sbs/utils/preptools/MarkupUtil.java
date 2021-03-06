package ch.sbs.utils.preptools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
	* Copyright (C) 2010 Swiss Library for the Blind, Visually Impaired and Print Disabled
	*
	* This file is part of dtbook-preptools.
	* 	
	* dtbook-preptools is free software: you can redistribute it
	* and/or modify it under the terms of the GNU Lesser General Public
	* License as published by the Free Software Foundation, either
	* version 3 of the License, or (at your option) any later version.
	* 	
	* This program is distributed in the hope that it will be useful,
	* but WITHOUT ANY WARRANTY; without even the implied warranty of
	* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
	* Lesser General Public License for more details.
	* 	
	* You should have received a copy of the GNU Lesser General Public
	* License along with this program. If not, see
	* <http://www.gnu.org/licenses/>.
	*/

public class MarkupUtil {
	private final RegionSkipper skipper;

	public MarkupUtil(final RegionSkipper theRegionSkipperComponent) {
		skipper = theRegionSkipperComponent;
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
	public Match find(final String text, int start, final Pattern pattern) {
		final Matcher m = pattern.matcher(text);
		boolean inSkipRegion = true;
		skipper.findRegionsToSkip(text);
		while (inSkipRegion && m.find(start)) {
			start = m.start() + 1;
			inSkipRegion = skipper.inSkipRegion(m);

		}
		return inSkipRegion ? Match.NULL_MATCH : new Match(m.start(), m.end());
	}

	/**
	 * Indicates whether pattern matches entire region
	 * 
	 * @param region
	 * @param pattern
	 * @return
	 */
	public static boolean matches(final String region, final Pattern pattern) {
		return region != null && pattern.matcher(region).matches();
	}

	/**
	 * Utility method to wrap a given string with a given element, i.e.
	 * surrounding it with opening/closing tags.
	 * 
	 * @param theString
	 *            the String to surround with tags
	 * @param theElement
	 *            the element name for which to create opening and closing tags.
	 * @return
	 */
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

	public static String getClosingTag(final String openingTag) {
		return openingTag.split("\\\\|\\s+")[0];
	}

}
