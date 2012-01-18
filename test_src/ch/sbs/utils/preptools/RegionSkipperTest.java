package ch.sbs.utils.preptools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import ch.sbs.plugin.preptools.VFormActionHelper;
import ch.sbs.utils.preptools.vform.VFormUtil;

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

public class RegionSkipperTest {

	@Test
	public void testSkip() {
		final String tag = "brl:v-form";
		final RegionSkipper theRegionSkipper = RegionSkipper
				.makeMarkupRegionSkipper(tag);
		final MarkupUtil mu = new MarkupUtil(theRegionSkipper);
		final Match m = mu.find("Sieben können " + MarkupUtil.wrap("Sie", tag)
				+ " haben.", 0, VFormUtil.get3rdPPPattern());
		assertEquals(Match.NULL_MATCH, m);
	}

	@Test
	public void testSkipLiteral() {
		final RegionSkipper theRegionSkipper = RegionSkipper
				.getDefaultSkipper();
		theRegionSkipper.addPattern(RegionSkipper
				.makeMarkupRegex(VFormActionHelper.VFORM_TAG));
		final MarkupUtil mu = new MarkupUtil(theRegionSkipper);
		final Match m = mu.find(
				"Sieben können " + MarkupUtil.wrap("Sie", "brl:literal")
						+ " haben.", 0, VFormUtil.get3rdPPPattern());
		assertEquals(Match.NULL_MATCH, m);
	}

	@Test
	public void testLiteralSkipper() {
		final RegionSkipper literalSkipper = RegionSkipper.getLiteralSkipper();
		final String theText = "\nhallo\n<brl:literal>\ndu\n</brl:literal>\nhier\n";
		literalSkipper.findRegionsToSkip(theText);
		assertFalse(literalSkipper.inSkipRegion(makeMatcher("hallo", theText)));
		assertTrue(literalSkipper.inSkipRegion(makeMatcher("du", theText)));
	}

	@Test
	public void testCommentSkipper() {
		final RegionSkipper commentSkipper = RegionSkipper.getCommentSkipper();
		final String theText = "\nhallo\n<!--\ndu\n-->\nhier\n";
		commentSkipper.findRegionsToSkip(theText);
		assertFalse(commentSkipper.inSkipRegion(makeMatcher("hallo", theText)));
		assertTrue(commentSkipper.inSkipRegion(makeMatcher("du", theText)));
	}

	@Test
	public void testLiteralSkipper1() {
		final RegionSkipper literalSkipper = RegionSkipper
				.getDefaultSkipper();
		final String theText = "\nhallo\n<brl:literal>\ndu\n</brl:literal>\nhier\n";
		literalSkipper.findRegionsToSkip(theText);
		assertFalse(literalSkipper.inSkipRegion(makeMatcher("hallo", theText)));
		assertTrue(literalSkipper.inSkipRegion(makeMatcher("du", theText)));
	}

	@Test
	public void testCommentSkipper1() {
		final RegionSkipper commentSkipper = RegionSkipper
				.getDefaultSkipper();
		final String theText = "\nhallo\n<!--\ndu\n-->\nhier\n";
		commentSkipper.findRegionsToSkip(theText);
		assertFalse(commentSkipper.inSkipRegion(makeMatcher("hallo", theText)));
		assertTrue(commentSkipper.inSkipRegion(makeMatcher("du", theText)));
	}

	@Test
	public void testHeaderSkipper1() {
		final RegionSkipper commentSkipper = RegionSkipper
				.getDefaultSkipper();
		final String theText = "bla hola bla tu bla <book>\nhallo\n\ndu\n\nhier\n</book>";
		commentSkipper.findRegionsToSkip(theText);
		assertTrue(commentSkipper.inSkipRegion(makeMatcher("hola", theText)));
		assertTrue(commentSkipper.inSkipRegion(makeMatcher("tu", theText)));
		assertFalse(commentSkipper.inSkipRegion(makeMatcher("hallo", theText)));
		assertFalse(commentSkipper.inSkipRegion(makeMatcher("du", theText)));
	}

	@Test
	public void testHeaderSkipper() {
		final RegionSkipper commentSkipper = RegionSkipper
				.getDefaultSkipper();
		final String theText = "\nbonsoir\n\nvous\n\nla\n<book>\nhallo\n\ndu\n\nhier\n</book>";
		commentSkipper.findRegionsToSkip(theText);
		assertTrue(commentSkipper.inSkipRegion(makeMatcher("bonsoir", theText)));
		assertTrue(commentSkipper.inSkipRegion(makeMatcher("vous", theText)));
		assertTrue(commentSkipper.inSkipRegion(makeMatcher("la", theText)));
		assertFalse(commentSkipper.inSkipRegion(makeMatcher("hallo", theText)));
		assertFalse(commentSkipper.inSkipRegion(makeMatcher("du", theText)));
		assertFalse(commentSkipper.inSkipRegion(makeMatcher("hier", theText)));
	}

	private static Matcher makeMatcher(final String thePattern,
			final String theText) {
		final Matcher matcher = Pattern.compile(thePattern).matcher(theText);
		matcher.find();
		return matcher;
	}
}
