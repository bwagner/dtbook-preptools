package ch.sbs.plugin.preptools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import org.junit.Test;

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

public class EmailRegexTest {

	@Test
	public void testEmail01() {
		final Pattern pattern = Pattern.compile(PrepToolLoader.EMAIL_URL_SEARCH_REGEX);
		assertTrue(pattern.matcher("max.mueller@sunrise.ch").find());
		assertEquals(
				"<p><dtb:a href=\"mailto:max.mueller@sunrise.ch\" external=\"true\">max.mueller@sunrise.ch</dtb:a></p>",
				UrlChangeAction.changeUrlOrEmail(pattern,
						"<p>max.mueller@sunrise.ch</p>"));

	}

	@Test
	public void testEmail03() {
		final Pattern pattern = Pattern.compile(PrepToolLoader.EMAIL_URL_SEARCH_REGEX);

		assertTrue(pattern.matcher("<p>mueller23@sunrise.mydot.com</p>").find());
		assertTrue(pattern.matcher("<p>hans_meier@swisscom.ch</p>").find());
		assertTrue(pattern.matcher("<p>mailto:hans.fischer@abb.ca</p>").find());
		assertFalse(pattern.matcher("<p>.com</p>").find());
		assertFalse(pattern.matcher("<p>@mario24</p>").find());

	}

	@Test
	public void testUrl01() {
		final Pattern pattern = Pattern.compile(PrepToolLoader.EMAIL_URL_SEARCH_REGEX);
		assertTrue(pattern.matcher("<p>http://123.245.3.4:4000?a=b</p>").find());
		assertEquals(
				"<p><dtb:a href=\"http://123.245.3.4:4000?a=b\" external=\"true\">http://123.245.3.4:4000?a=b</dtb:a></p>",
				UrlChangeAction.changeUrlOrEmail(pattern,
						"<p>http://123.245.3.4:4000?a=b</p>"));

	}

	@Test
	public void testUrl02() {
		final Pattern pattern = Pattern.compile(PrepToolLoader.EMAIL_URL_SEARCH_REGEX);
		assertFalse(pattern.matcher("<p>123.245.3.4:4000?a=b</p>").find());
		assertEquals(
				"<p><dtb:a href=\"http://foo.bar:4000?a=b\" external=\"true\">foo.bar:4000?a=b</dtb:a></p>",
				UrlChangeAction.changeUrlOrEmail(pattern,
						"<p>foo.bar:4000?a=b</p>"));

	}

	@Test
	public void testUrl03() {
		final Pattern pattern = Pattern.compile(PrepToolLoader.EMAIL_URL_SEARCH_REGEX);
		assertTrue(pattern.matcher("<p>www.beispiel.com</p>").find());
		assertTrue(pattern.matcher("<p>http://1.2.3.4</p>").find());
		assertFalse(pattern.matcher("<p>http://xmlp-test/todo/139</p>").find());
		assertTrue(pattern.matcher("<p>http://xmlp-test.com/todo/139</p>")
				.find());
		assertTrue(pattern.matcher("<p>adventures.com</p>").find());
		assertTrue(pattern.matcher("<p>adventures.hu</p>").find());
		assertTrue(pattern.matcher("<p>http://123.245.3.4:4000?a=b</p>").find());
		assertFalse(pattern.matcher("<p>123.245.3.4:4000?a=b</p>").find());

	}
}
