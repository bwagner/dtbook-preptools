package ch.sbs.plugin.preptools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import org.junit.Test;

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

public class RegexAbbrevTest {

	@Test
	public void testAbbrevPeriod() {
		final Pattern pattern = Pattern
				.compile(PrepToolLoader.ABBREV_SEARCH_REGEX);
		assertTrue(pattern.matcher("z.B.").find());
		assertTrue(pattern.matcher("z. B.").find());
		assertTrue(pattern.matcher("dipl. Inf.").find());
		assertTrue(pattern.matcher("a.\n          b.").find());
		assertTrue(pattern.matcher("Z").find());
		assertTrue(Pattern.compile(PrepToolLoader.ABBREV_SEARCH_REGEX)
				.matcher("a.b.").find());
	}

	@Test
	public void testAbbrevPeriodMoreChars() {
		final Pattern pattern = Pattern
				.compile(PrepToolLoader.ABBREV_SEARCH_REGEX);
		assertTrue(pattern.matcher("z.Å.").find());
	}

	@Test
	public void testAbbrevCapital() {
		final Pattern pattern = Pattern
				.compile(PrepToolLoader.ABBREV_SEARCH_REGEX);
		assertTrue(pattern.matcher("bloss A4 brauchen").find());
		assertEquals("bloss <abbr>A</abbr>4 brauchen",
				AbbrevChangeAction.changeAbbrevFollowedByDigit(pattern,
						"bloss A4 brauchen"));
		assertTrue(pattern.matcher("drum A geben").find());
		assertTrue(pattern.matcher("drumA454 geben").find());
	}

	@Test
	public void testAbbrevCapitalMoreChars() {
		final Pattern pattern = Pattern
				.compile(PrepToolLoader.ABBREV_SEARCH_REGEX);
		assertTrue(pattern.matcher("bloss É4 brauchen").find());
	}

	@Test
	public void testAbbrevAcronym() {
		final Pattern pattern = Pattern
				.compile(PrepToolLoader.ABBREV_SEARCH_REGEX);
		assertTrue(pattern.matcher("Die GSoA ist").find());
		assertTrue(pattern.matcher("ein mE guter").find());
		assertFalse(pattern.matcher("ein Arbeiten").find());
	}

	@Test
	public void testAbbrevAcronymMoreChars() {
		final Pattern pattern = Pattern
				.compile(PrepToolLoader.ABBREV_SEARCH_REGEX);
		assertTrue(pattern.matcher("Die GSöA ist").find());
		assertTrue(pattern.matcher("ein mÉ guter").find());
	}

	@Test
	public void testChangeAbbrevFollowedByDigit() {
		final Pattern pattern = Pattern
				.compile(PrepToolLoader.ABBREV_SEARCH_REGEX);

		assertTrue(pattern.matcher("blabla Abb. 232 blabla").find());
		assertEquals("blabla <abbr>Abb. </abbr>232 blabla",
				AbbrevChangeAction.changeAbbrevFollowedByDigit(pattern,
						"blabla Abb. 232 blabla"));

		assertTrue(pattern.matcher("blabla Nr. 3 blabla").find());
		assertEquals("blabla <abbr>Nr. </abbr>3 blabla",
				AbbrevChangeAction.changeAbbrevFollowedByDigit(pattern,
						"blabla Nr. 3 blabla"));

		assertTrue(pattern.matcher("blabla Bd. 33 blabla").find());
		assertEquals("blabla <abbr>Bd. </abbr>33 blabla",
				AbbrevChangeAction.changeAbbrevFollowedByDigit(pattern,
						"blabla Bd. 33 blabla"));

		// Feature 1628: Match single capital letters in Abbreviation-PrepTool
		assertTrue(pattern.matcher("S. 232").find());
		assertEquals("blabla <abbr>S. </abbr>3 blabla",
				AbbrevChangeAction.changeAbbrevFollowedByDigit(pattern,
						"blabla S. 3 blabla"));
	}

}
