package ch.sbs.utils.preptools;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import org.junit.Test;

import ch.sbs.plugin.preptools.PrepToolLoader;

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
}
