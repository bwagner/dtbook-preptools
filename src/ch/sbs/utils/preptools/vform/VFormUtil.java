package ch.sbs.utils.preptools.vform;

import java.util.regex.Pattern;

import ch.sbs.utils.preptools.MarkupUtil;

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

public class VFormUtil {
	// Mail von Mischa Kuenzle 12.1.2011 15:09
	// 3. Person PL (obligatorische Abfrage)
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

	private static final String[] forms3rdPersonPlural = new String[] { WordHierarchyBuilder
			.createWordTree(thirdPP).toRegex() };

	private static final String[] forms2ndPerson = new String[] { WordHierarchyBuilder
			.createWordTree(secondP).toRegex() };

	private static final String[] allForms;

	static {
		allForms = new String[forms3rdPersonPlural.length
				+ forms2ndPerson.length];
		int i = 0;
		for (final String[] forms : new String[][] { forms3rdPersonPlural,
				forms2ndPerson }) {
			for (final String form : forms) {
				allForms[i++] = form;
			}
		}
	}

	private static Pattern vFormDefaultPattern;

	private static final Pattern vFormPatternAll;
	private static final Pattern vFormPattern3rdPersonPlural;

	static {
		vFormPatternAll = makePattern(allForms);
		vFormPattern3rdPersonPlural = makePattern(forms3rdPersonPlural);
		vFormDefaultPattern = vFormPatternAll;
	}

	public static Pattern get3rdPPPattern() {
		return vFormPattern3rdPersonPlural;
	}

	public static Pattern getAllPattern() {
		return vFormPatternAll;
	}

	private static Pattern makePattern(final String[] forms) {
		final StringBuffer sb = new StringBuffer("(?:"); // non-capturing group,
															// see
		// http://download.oracle.com/javase/1.5.0/docs/api/java/util/regex/Pattern.html#special
		for (final String form : forms) {
			sb.append(form);
			sb.append("|");
		}
		sb.setLength(sb.length() - 1); // chop off last "|"
		sb.append(")\\b"); // make sure we don't match substrings.
		return Pattern.compile(sb.toString());
	}

	/**
	 * Does the same as matches for the default pattern.
	 * 
	 * @param text
	 * @return
	 */
	public static boolean matches(final String text) {
		return MarkupUtil.matches(text, vFormDefaultPattern);
	}
}
