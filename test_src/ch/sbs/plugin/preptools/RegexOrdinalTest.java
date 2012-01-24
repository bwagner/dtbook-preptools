package ch.sbs.plugin.preptools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.junit.Test;

import ch.sbs.utils.preptools.DocumentTestUtil;
import ch.sbs.utils.preptools.DocumentUtils;

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

public class RegexOrdinalTest {

	@Test
	public void testOrdinal() {
		final Pattern pattern = Pattern
				.compile(PrepToolLoader.ORDINAL_SEARCH_REGEX);
		assertTrue(pattern.matcher("5.").find());
		assertFalse(pattern.matcher("a5.").find());
		assertTrue(pattern.matcher("23423.").find());
		assertFalse(pattern.matcher("2342").find());
	}

	@Test
	public void testBug1275Match() {
		final Pattern pattern = Pattern
				.compile(PrepToolLoader.ORDINAL_SEARCH_REGEX);
		assertTrue(pattern.matcher("bla 23. bla").find());
		assertTrue(pattern.matcher("bla 345000. bla").find());
		assertTrue(pattern.matcher("bla 1. bla").find());
		assertTrue(pattern.matcher("bla 0. bla").find());
		assertTrue(pattern.matcher("bla 1.-9. bla").find());
	}

	@Test
	public void testBug1275NoMatch() {
		final Pattern pattern = Pattern
				.compile(PrepToolLoader.ORDINAL_SEARCH_REGEX);
		assertFalse(pattern.matcher("bla 34,4.5 bla").find());
		assertFalse(pattern.matcher("bla 2,4. bla").find());
		assertFalse(pattern.matcher("bla 45'44. bla").find());
		assertFalse(pattern.matcher("bla 345'000. bla").find());
		assertFalse(pattern.matcher("bla 23.00 bla").find());
		assertFalse(pattern.matcher("bla 45.0 bla").find());
		assertFalse(pattern.matcher("bla 45.34,50 bla").find());
		assertFalse(pattern.matcher("bla 100.000.000. bla").find());
	}

	@Test
	public void testAppendNbspToOrdinal_ndsp_newline_noun() {
		final String input = "bla 23.\nbla";
		final Pattern pattern = Pattern
				.compile(PrepToolLoader.ORDINAL_SEARCH_REGEX);
		assertEquals("bla <brl:num role=\"ordinal\">23.</brl:num>&nbsp;bla",
				OrdinalChangeAction.appendNbspToOrdinal(pattern, input));
	}

	@Test
	public void testAppendNbspToOrdinal_ndsp_blank_noun() {
		final String input = "bla 23. bla";
		final Pattern pattern = Pattern
				.compile(PrepToolLoader.ORDINAL_SEARCH_REGEX);
		assertEquals("bla <brl:num role=\"ordinal\">23.</brl:num>&nbsp;bla",
				OrdinalChangeAction.appendNbspToOrdinal(pattern, input));
	}

	@Test
	public void testAppendNbspToOrdinal_no_ndsp_no_blank() {
		final String input = "bla 23.bla";
		final Pattern pattern = Pattern
				.compile(PrepToolLoader.ORDINAL_SEARCH_REGEX);
		assertEquals("bla <brl:num role=\"ordinal\">23.</brl:num>bla",
				OrdinalChangeAction.appendNbspToOrdinal(pattern, input));
	}

	@Test
	public void testAppendNbspToOrdinal_no_ndsp_blank_p() {
		final String input = "<p>am 25. </p>";
		final Pattern pattern = Pattern
				.compile(PrepToolLoader.ORDINAL_SEARCH_REGEX);
		assertEquals("<p>am <brl:num role=\"ordinal\">25.</brl:num> </p>",
				OrdinalChangeAction.appendNbspToOrdinal(pattern, input));
	}

	@Test
	public void testAppendNbspToOrdinal_no_ndsp_ndash() {
		final String input = "<p>am 25. &ndash; blablabla</p>";
		final Pattern pattern = Pattern
				.compile(PrepToolLoader.ORDINAL_SEARCH_REGEX);
		assertEquals(
				"<p>am <brl:num role=\"ordinal\">25.</brl:num> &ndash; blablabla</p>",
				OrdinalChangeAction.appendNbspToOrdinal(pattern, input));
	}

	@Test
	public void testDocUtilAppendNbspToOrdinal() throws BadLocationException {
		final StringBuilder sb1 = new StringBuilder();
		final StringBuilder sb2 = new StringBuilder();
		sb1.append("1.\n        ");
		sb2.append("<brl:num role=\"ordinal\">1.</brl:num>&nbsp;");
		final Document document = DocumentTestUtil.makeDocument(sb1.toString());
		final Pattern pattern = Pattern
				.compile(PrepToolLoader.ORDINAL_SEARCH_REGEX);
		final String appendNbspToOrdinal = OrdinalChangeAction
				.appendNbspToOrdinal(pattern, sb1.toString());
		final int count = DocumentUtils.performMultipleReplacements(document,
				PrepToolLoader.ORDINAL_SEARCH_REGEX, appendNbspToOrdinal);
		assertEquals(sb2.toString(), document.getText(0, document.getLength()));
		assertEquals(1, count);

	}
}
