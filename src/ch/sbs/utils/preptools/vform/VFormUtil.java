package ch.sbs.utils.preptools.vform;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import net.xmlizer.wordhierarchy.WordHierarchyBuilder;
import ch.sbs.utils.preptools.MarkupUtil;

/**
 * Copyright (C) 2010 Swiss Library for the Blind, Visually Impaired and Print
 * Disabled
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

public class VFormUtil {
	// Mail von Mischa Kuenzle 12.1.2011 15:09
	// 1. Person PL (obligatorische Abfrage)
	private static final String[] thirdPP = new String[] { "Ihnen", "Ihr",
			"Ihre", "Ihrem", "Ihren", "Ihrer", "Ihrerseits", "Ihres",
			"Ihresgleichen", "Ihrethalben", "Ihretwegen", "Ihretwillen",
			"Ihrige", "Ihrigem", "Ihrigen", "Ihriger", "Ihriges", "Ihrs",
			"Sie", };

	// Mail von Mischa Kuenzle 12.1.2011 15:09
	// Mail von Mischa Kuenzle 8.6.2011 10:53
	// 2. Person (optionale Abfrage)
	private static final String[] secondP = new String[] { "Dein", "Deine",
			"Deinem", "Deinen", "Deiner", "Deinerseits", "Deines",
			"Deinesgleichen", "Deinethalben", "Deinetwegen", "Deinetwillen",
			"Deinige", "Deinigem", "Deinigen", "Deiniger", "Deiniges", "Deins",
			"Dich", "Dir", "Du", "Euch", "Euer", "Euere", "Euerem", "Euerer",
			"Eueres", "Euers", "Euerseits", "Eure", "Eurem", "Euren", "Eurer",
			"Euerm", "Euern", "Eueren", "Eurerseits", "Eures", "Euresgleichen",
			"Eurethalben", "Euretwegen", "Euretwillen", "Eurige", "Eurigem",
			"Eurigen", "Euriger", "Euriges", };

	private static final Pattern vFormPatternAll;
	private static final Pattern vFormPattern3rdPersonPlural;

	static {
		final Set<String> allForms = new HashSet<String>(thirdPP.length
				+ secondP.length);
		allForms.addAll(Arrays.asList(thirdPP));
		allForms.addAll(Arrays.asList(secondP));
		vFormPatternAll = Pattern
				.compile(wrapInWordBoundaries(WordHierarchyBuilder
						.createWordTree(allForms).toRegex()));
		vFormPattern3rdPersonPlural = Pattern
				.compile(wrapInWordBoundaries(WordHierarchyBuilder
						.createWordTree(thirdPP).toRegex()));
	}

	private static String wrapInWordBoundaries(final String pattern) {
		return "(?:" + pattern + ")\\b";
	}

	public static Pattern get3rdPPPattern() {
		return vFormPattern3rdPersonPlural;
	}

	public static Pattern getAllPattern() {
		return vFormPatternAll;
	}

	/**
	 * Does the same as matches for the all pattern.
	 * 
	 * @param text
	 *            text to match
	 * @return true if text matches all pattern
	 */
	public static boolean matchesAll(final String text) {
		return MarkupUtil.matches(text, vFormPatternAll);
	}
}
