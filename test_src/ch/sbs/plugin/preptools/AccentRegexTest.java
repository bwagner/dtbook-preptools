package ch.sbs.plugin.preptools;

import static org.junit.Assert.assertEquals;

import java.util.regex.Pattern;

import org.junit.Test;

public class AccentRegexTest {
	private static final String inputReduced = "<span brl:accents=\"reduced\">Térezia</span>";
	private static final String inputDetailed = "<span brl:accents=\"detailed\">Térezia</span>";
	private static final String inputTwiceSameline = "<span brl:accents=\"detailed\">Térezia</span> bla <span brl:accents=\"detailed\">Térezia</span>";
	private static final String inputTwiceNewline = "<span brl:accents=\"detailed\">Térezia</span> \n <span brl:accents=\"detailed\">Térezia</span>";

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
				"Pattern AccentChangeAction.REGEX_SPAN_DETAILED works when occurring twice on a line.",
				"Térezia bla Térezia",
				Pattern.compile(AccentChangeAction.REGEX_SPAN_DETAILED)
						.matcher(inputTwiceSameline)
						.replaceAll(AccentChangeAction.REPLACE));
	}

	@Test
	public void testAccentRegexDetailedTwiceNewline() {
		assertEquals(
				"Pattern AccentChangeAction.REGEX_SPAN_DETAILED works on newlines.",
				"Térezia \n Térezia",
				Pattern.compile(AccentChangeAction.REGEX_SPAN_DETAILED)
						.matcher(inputTwiceNewline)
						.replaceAll(AccentChangeAction.REPLACE));
	}

	@Test
	public void testCapitalLetters1() {
		final String string = "Frau évora ist kompetent.";
		final String exp = "Frau <span brl:accents=\"_____\">évora</span> ist kompetent.";
		assertEquals(
				"Pattern PrepToolLoader.ACCENT_REGEX works on accented chars.",
				exp,
				Pattern.compile(PrepToolLoader.ACCENT_SEARCH_REGEX).matcher(string)
						.replaceAll(PrepToolLoader.ACCENT_REPLACE));
	}

	@Test
	public void testCapitalLettersUnicode_flag() {
		final String string = "Frau Évora ist kompetent.";
		final String exp = "Frau <span brl:accents=\"_____\">Évora</span> ist kompetent.";
		assertEquals(
				"Pattern.UNICODE_CASE can be set via flag to compile, too.",
				exp,
				Pattern.compile(PrepToolLoader.ACCENT_SEARCH_REGEX.replace("u", ""),
						Pattern.UNICODE_CASE).matcher(string)
						.replaceAll(PrepToolLoader.ACCENT_REPLACE));
	}

	@Test
	public void testCapitalLetters2Unicode_uRange() {
		final String string = "Frau Évora ist kompetent.";
		final String exp = "Frau <span brl:accents=\"_____\">Évora</span> ist kompetent.";
		assertEquals(
				"Pattern.UNICODE_CASE can surround the regex, too, using (?u: <regex> )",
				exp,
				Pattern.compile(
						"(?u:" + PrepToolLoader.ACCENT_SEARCH_REGEX.replace("u", "")
								+ ")").matcher(string)
						.replaceAll(PrepToolLoader.ACCENT_REPLACE));
	}

	@Test
	public void testCapitalLettersUnicode_uSwitch() {
		final String string = "Frau Évora ist kompetent.";
		final String exp = "Frau <span brl:accents=\"_____\">Évora</span> ist kompetent.";
		assertEquals(
				"Pattern.UNICODE_CASE can be prepended, too, using (?u)",
				exp,
				Pattern.compile(
						"(?u)" + PrepToolLoader.ACCENT_SEARCH_REGEX.replace("u", ""))
						.matcher(string)
						.replaceAll(PrepToolLoader.ACCENT_REPLACE));
	}

	@Test
	public void testCapitalLettersUnicode_no() {
		final String string = "Frau Évora ist kompetent.";
		assertEquals(
				"Pattern.CASE_INSENSITIVE flag without Pattern.UNICODE_CASE doesn't work on É",
				string,
				Pattern.compile(PrepToolLoader.ACCENT_SEARCH_REGEX.replace("u", ""))
						.matcher(string)
						.replaceAll(PrepToolLoader.ACCENT_REPLACE));
	}

	@Test
	public void testCapitalLettersUnicode_twice() {
		final String string = "Er verkehrt im Élysée-Palast.";
		final String exp = "Er verkehrt im <span brl:accents=\"_____\">Élysée</span>-Palast.";
		assertEquals("two unicode characters in one word", exp, Pattern
				.compile(PrepToolLoader.ACCENT_SEARCH_REGEX).matcher(string)
				.replaceAll(PrepToolLoader.ACCENT_REPLACE));
	}

	@Test
	public void testCapitalLettersUnicode_twiceTilde() {
		final String string = "Er verkehrt im Éspaña-Palast.";
		final String exp = "Er verkehrt im <span brl:accents=\"_____\">Éspaña</span>-Palast.";
		assertEquals("two unicode characters in one word", exp, Pattern
				.compile(PrepToolLoader.ACCENT_SEARCH_REGEX).matcher(string)
				.replaceAll(PrepToolLoader.ACCENT_REPLACE));
	}

	@Test
	public void testCapitalLettersUnicode_no2() {
		final String string = "Frau Évora ist kompetent.";
		assertEquals(
				"Pattern.UNICODE_CASE flag without Pattern.CASE_INSENSITIVE does nothing",
				string,
				Pattern.compile(PrepToolLoader.ACCENT_SEARCH_REGEX.replace("i", ""))
						.matcher(string)
						.replaceAll(PrepToolLoader.ACCENT_REPLACE));
	}

}
