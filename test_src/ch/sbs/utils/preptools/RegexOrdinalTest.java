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

}
