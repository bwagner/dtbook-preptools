package ch.sbs.utils.preptools;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TextUtilsTest {
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

	@Test
	public void testI() {
		assertEquals("(?i:bla)", TextUtils.wrapI("bla"));
	}

	@Test
	public void testMultiline() {
		assertEquals("(?m:bla)", TextUtils.wrapMultiline("bla"));
	}

	@Test
	public void testDotAll() {
		assertEquals("(?s:bla)", TextUtils.wrapDotAll("bla"));
	}

	@Test
	public void testUI() {
		assertEquals("(?u:(?i:bla))", TextUtils.wrapIU("bla"));
	}

	@Test
	public void testX() {
		assertEquals("(?x:bla)", TextUtils.wrapComments("bla"));
	}

	@Test
	public void testProtectForSearchString() {
		final String txt = "Dieser Text enthält leider keine Klammer. Das tut ihm Leid.Restlos.";
		final String regex = "leid.r";
		assertEquals(
				"Dieser Text enthält Leid. R keine Klammer. Das tut ihm Leid. Restlos.",
				txt.replaceAll(TextUtils.wrapI(regex), "Leid. R"));
		assertEquals(
				"Dieser Text enthält leider keine Klammer. Das tut ihm Leid. Restlos.",
				txt.replaceAll(
						TextUtils.wrapI(TextUtils.quoteRegexMeta(regex)),
						"Leid. R"));
	}

	@Test(expected = AssertionError.class)
	public void testProtectDoesNotWorkForReplacementString() {
		assertEquals("b-$1-a",
				"bla".replaceAll("(l)", TextUtils.quoteRegexMeta("-$1-")));
	}
}
