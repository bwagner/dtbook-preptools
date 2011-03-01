package ch.sbs.plugin.preptools;

import static org.junit.Assert.assertEquals;

import java.util.regex.Pattern;

import org.junit.Test;

public class AccentRegexTest {
	private static final String inputReduced = "<span brl:accents=\"reduced\">Térezia</span>";
	private static final String inputDetailed = "<span brl:accents=\"detailed\">Térezia</span>";
	private static final String inputTwiceSameline = "<span brl:accents=\"detailed\">Térezia</span> bla <span brl:accents=\"detailed\">Térezia</span>";

	@Test
	public void testAccentRegexReduced() {
		assertEquals(
				"Térezia",
				Pattern.compile(AccentChangeAction.REGEX_SPAN_REDUCED)
						.matcher(inputReduced)
						.replaceAll(AccentChangeAction.REPLACE));
	}

	@Test
	public void testAccentRegexNoReduced() {
		assertEquals(
				inputDetailed,
				Pattern.compile(AccentChangeAction.REGEX_SPAN_REDUCED)
						.matcher(inputDetailed)
						.replaceAll(AccentChangeAction.REPLACE));
	}

	@Test
	public void testAccentRegexDetailed() {
		assertEquals(
				"Térezia",
				Pattern.compile(AccentChangeAction.REGEX_SPAN_DETAILED)
						.matcher(inputDetailed)
						.replaceAll(AccentChangeAction.REPLACE));
	}

	@Test
	public void testAccentRegexNoDetailed() {
		assertEquals(
				inputReduced,
				Pattern.compile(AccentChangeAction.REGEX_SPAN_DETAILED)
						.matcher(inputReduced)
						.replaceAll(AccentChangeAction.REPLACE));
	}

	@Test
	public void testAccentRegexDetailedTwice() {
		assertEquals(
				"Térezia bla Térezia",
				Pattern.compile(AccentChangeAction.REGEX_SPAN_DETAILED)
						.matcher(inputTwiceSameline)
						.replaceAll(AccentChangeAction.REPLACE));
	}
}
