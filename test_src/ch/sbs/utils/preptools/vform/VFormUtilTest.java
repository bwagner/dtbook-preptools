package ch.sbs.utils.preptools.vform;

import static ch.sbs.utils.preptools.vform.VFormUtil.wrap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ch.sbs.utils.preptools.Match;
import ch.sbs.utils.preptools.vform.VFormUtil;

public class VFormUtilTest {

	@Test
	public void testMatchesNull() {
		assertFalse(VFormUtil.matches(null));
	}

	@Test
	public void testReplaceMatches() {
		assertTrue(VFormUtil.matches("Deine"));
		assertFalse(VFormUtil.matches("Sieb"));
	}

	@Test
	public void testReplaceKeepPart() {
		assertEquals("Deintegration", VFormUtil.replace("Deintegration"));
		assertEquals("Sieb", VFormUtil.replace("Sieb"));
	}

	@Test
	public void testReplaceKeep() {
		assertEquals(Match.NULL_MATCH, VFormUtil.find("Sieb", 0));
		final Match m = VFormUtil.find("Sieben können Sie haben.", 0);
		assertEquals(14, m.startOffset);
		assertEquals(14 + "Sie".length(), m.endOffset);
	}

	@Test
	public void testReplaceNothing() {
		assertEquals("nix", VFormUtil.replace("nix"));
	}

	@Test
	public void testDeinetwegen() {
		assertEquals(wrap("Deinetwegen") + " ist es schief gegangen.",
				VFormUtil.replace("Deinetwegen ist es schief gegangen."));
	}

	@Test
	public void testReplace() {
		assertEquals(wrap("Sie"), VFormUtil.replace("Sie"));
		assertEquals(wrap("Ihre"), VFormUtil.replace("Ihre"));
		assertEquals(wrap("Ihr"), VFormUtil.replace("Ihr"));
		assertEquals(wrap("Ihren"), VFormUtil.replace("Ihren"));
		assertEquals(wrap("Ihrem"), VFormUtil.replace("Ihrem"));
		assertEquals(wrap("Ihres"), VFormUtil.replace("Ihres"));
		assertEquals(wrap("Deine"), VFormUtil.replace("Deine"));
		assertEquals(wrap("Dein"), VFormUtil.replace("Dein"));
		assertEquals("Das können " + wrap("Sie") + " zu " + wrap("Ihren")
				+ " Akten legen.",
				VFormUtil.replace("Das können Sie zu Ihren Akten legen."));
	}

	@Test
	public void testMatch() {

		// ____________________________1___________________2
		// __________________01234567890123456789012345678901234
		final String text = "Das können Sie zu Ihren Akten legen.";

		Match match = VFormUtil.find(text, 0);

		assertEquals(11, match.startOffset);
		assertEquals(14, match.endOffset);

		match = VFormUtil.find(text, match.endOffset);

		assertEquals(18, match.startOffset);
		assertEquals(23, match.endOffset);

		match = VFormUtil.find(text, match.endOffset);

		assertEquals(Match.NULL_MATCH, match);

	}

	@Test
	public void testMatchBoundary() {

		// ____________1___________________2
		// ____________01234567890123456789012345678901234
		final String text = "Dann können Sie's Ihrem Kollegen geben.";

		Match match = VFormUtil.find(text, 0);

		assertEquals(12, match.startOffset);
		assertEquals(15, match.endOffset);

		match = VFormUtil.find(text, match.endOffset);

		assertEquals(18, match.startOffset);
		assertEquals(23, match.endOffset);

		match = VFormUtil.find(text, match.endOffset);

		assertEquals(Match.NULL_MATCH, match);

	}

	@Test
	public void testNoMatch() {

		final String text = "Dann kann Anna es ihrem Kollegen geben.";

		Match match = VFormUtil.find(text, 0);

		assertEquals(Match.NULL_MATCH, match);

	}

	@Test
	public void testMatch1() {

		// ____________________________1_________2_________3
		// __________________01234567890123456789012345678901234
		final String text = "Dann kann Anna es Ihrem Kollegen geben.";

		Match match = VFormUtil.find(text, 0);

		assertEquals(18, match.startOffset);
		assertEquals(23, match.endOffset);

	}

	@Test
	public void testNoMatch1() {

		final String text = "Dann kann Anna es <brl:v-form>Ihrem</brl:v-form> Kollegen geben.";

		Match match = VFormUtil.find(text, 0);

		assertEquals(Match.NULL_MATCH, match);

	}

	@Test
	public void testEur() {

		assertEquals("Das kostet 50 Eur.",
				VFormUtil.replace("Das kostet 50 Eur."));
		assertEquals("Was kostet " + wrap("Eure") + " Lösung?",
				VFormUtil.replace("Was kostet Eure Lösung?"));

	}

	/*
	 * 	EBNF für Satzende:
	--------------------
	
	 Variations:
	 1. Punkt                        Whitespace
	 2. Punkt                        Whitespace QuoteSign
	 3. Punkt QuoteSign              Whitespace
	 4. Punkt QuoteSign              Whitespace QuoteSign 
	 5. Punkt           ClosingBrace Whitespace
	 6. Punkt           ClosingBrace Whitespace QuoteSign 
	 7. Punkt           ClosingBrace            QuoteSign Whitespace
	 8. Punkt QuoteSign ClosingBrace Whitespace
	 9. Punkt QuoteSign              Whitespace QuoteSign 
	10. Punkt QuoteSign ClosingBrace Whitespace QuoteSign 
	 */

	@Test
	public void testSettingPatternAll() {
		// ____________________________1_________2_________3
		// __________________01234567890123456789012345678901234
		final String text = "Dann kann Anna es Deinem Kollegen geben.";

		Match match = VFormUtil.find(text, 0,
				VFormUtil.getAllPattern());

		assertEquals(18, match.startOffset);
		assertEquals(24, match.endOffset);

	}

	@Test
	public void testSettingPattern3rdPP() {
		// ____________________________1_________2_________3
		// __________________01234567890123456789012345678901234
		final String text = "Dann kann Anna es Deinem Kollegen geben.";

		Match match = VFormUtil.find(text, 0,
				VFormUtil.get3rdPPPattern());

		assertEquals(match, Match.NULL_MATCH);

	}
}
