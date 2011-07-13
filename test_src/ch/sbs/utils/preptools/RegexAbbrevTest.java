package ch.sbs.utils.preptools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import ch.sbs.plugin.preptools.PrepToolLoader;

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
				feature1414(pattern, "bloss A4 brauchen"));
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
	public void testFeature1414() {
		final Pattern pattern = Pattern
				.compile(PrepToolLoader.ABBREV_SEARCH_REGEX);

		assertTrue(pattern.matcher("blabla Abb. 232 blabla").find());
		assertEquals("blabla <abbr>Abb. </abbr>232 blabla",
				feature1414(pattern, "blabla Abb. 232 blabla"));

		assertTrue(pattern.matcher("blabla Nr. 3 blabla").find());
		assertEquals("blabla <abbr>Nr. </abbr>3 blabla",
				feature1414(pattern, "blabla Nr. 3 blabla"));

		assertTrue(pattern.matcher("blabla Bd. 33 blabla").find());
		assertEquals("blabla <abbr>Bd. </abbr>33 blabla",
				feature1414(pattern, "blabla Bd. 33 blabla"));
	}

	public static String feature1414(final Pattern pattern, final String input) {
		final Matcher matcher = pattern.matcher(input);
		matcher.find();
		assertEquals(3, matcher.groupCount());
		if (matcher.group(3) != null) {
			return pattern.matcher(input).replaceAll("<abbr>$2</abbr>$3");
		}
		else {
			return pattern.matcher(input).replaceAll("<abbr>$1</abbr>");
		}
	}
}
