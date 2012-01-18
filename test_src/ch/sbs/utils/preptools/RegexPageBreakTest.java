package ch.sbs.utils.preptools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import org.junit.Test;

import ch.sbs.plugin.preptools.PrepToolLoader;

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

public class RegexPageBreakTest {

	// public static final String PAGEBREAK_REGEX =
	// "</p>\\s*(<pagenum.*?</pagenum\\s*>)\\s*<p>";
	@Test
	public void test1() {
		final Pattern pattern = Pattern
				.compile(PrepToolLoader.PAGEBREAK_SEARCH_REGEX);
		final String inner = "<pagenum id=\"page-20\" page=\"normal\">20</pagenum>";
		final String input = "</p>\n\t" + inner + "\n\t<p>";
		assertTrue(pattern.matcher(input).find());
		assertEquals(
				" " + inner + " ",
				pattern.matcher(input).replaceAll(
						PrepToolLoader.PAGEBREAK_REPLACE));
	}

	@Test
	public void test2() {
		final Pattern pattern = Pattern
				.compile(PrepToolLoader.PAGEBREAK_SEARCH_REGEX);
		final String input = "</p>\n\t<pagenum id=\"page-20\" page=\"normal\">20</pagenum>\n\t<p>";
		assertTrue(pattern.matcher(input).find());
		assertEquals(
				" <pagenum id=\"page-20\" page=\"normal\">20</pagenum> ",
				pattern.matcher(input).replaceAll(
						PrepToolLoader.PAGEBREAK_REPLACE));
	}

	@Test
	public void testFeature1272() {
		final Pattern pattern = Pattern
				.compile(PrepToolLoader.PAGEBREAK_SEARCH_REGEX);
		final String input = "</p>\n\t<pagenum id=\"page-20\" page=\"normal\">20</pagenum>\n\t<p>";
		assertTrue(pattern.matcher(input).find());
		assertEquals(
				"</p>\n\t<pagenum id=\"page-20\" page=\"normal\">20</pagenum>\n\t<p class=\"precedingemptyline\">",
				pattern.matcher(input).replaceAll(
						PrepToolLoader.PAGEBREAK_REPLACE2));
	}

}
