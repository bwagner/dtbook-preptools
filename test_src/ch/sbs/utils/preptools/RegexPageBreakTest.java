package ch.sbs.utils.preptools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import org.junit.Test;

import ch.sbs.plugin.preptools.PrepToolLoader;

public class RegexPageBreakTest {

	// public static final String PAGEBREAK_REGEX =
	// "</p>\\s*(<pagenum.*?</pagenum\\s*>)\\s*<p>";
	@Test
	public void testPagebreak1() {
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
	public void testPagebreak2() {
		final Pattern pattern = Pattern
				.compile(PrepToolLoader.PAGEBREAK_SEARCH_REGEX);
		final String input = "</p>\n\t<pagenum id=\"page-20\" page=\"normal\">20</pagenum>\n\t<p>";
		assertTrue(pattern.matcher(input).find());
		assertEquals(
				" <pagenum id=\"page-20\" page=\"normal\">20</pagenum> ",
				pattern.matcher(input).replaceAll(
						PrepToolLoader.PAGEBREAK_REPLACE));
	}

}
