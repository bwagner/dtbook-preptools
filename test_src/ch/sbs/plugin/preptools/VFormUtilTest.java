package ch.sbs.plugin.preptools;

import static ch.sbs.utils.preptools.vform.VFormUtil.wrap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.junit.Test;

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
		assertEquals(VFormUtil.NULL_MATCH, VFormUtil.find("Sieb", 0));
		final VFormUtil.Match m = VFormUtil.find("Sieben können Sie haben.", 0);
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

		VFormUtil.Match match = VFormUtil.find(text, 0);

		assertEquals(11, match.startOffset);
		assertEquals(14, match.endOffset);

		match = VFormUtil.find(text, match.endOffset);

		assertEquals(18, match.startOffset);
		assertEquals(23, match.endOffset);

		match = VFormUtil.find(text, match.endOffset);

		assertEquals(VFormUtil.NULL_MATCH, match);

	}

	@Test
	public void testMatchBoundary() {

		// ____________1___________________2
		// ____________01234567890123456789012345678901234
		final String text = "Dann können Sie's Ihrem Kollegen geben.";

		VFormUtil.Match match = VFormUtil.find(text, 0);

		assertEquals(12, match.startOffset);
		assertEquals(15, match.endOffset);

		match = VFormUtil.find(text, match.endOffset);

		assertEquals(18, match.startOffset);
		assertEquals(23, match.endOffset);

		match = VFormUtil.find(text, match.endOffset);

		assertEquals(VFormUtil.NULL_MATCH, match);

	}

	@Test
	public void testNoMatch() {

		final String text = "Dann kann Anna es ihrem Kollegen geben.";

		VFormUtil.Match match = VFormUtil.find(text, 0);

		assertEquals(VFormUtil.NULL_MATCH, match);

	}

	@Test
	public void testMatch1() {

		// ____________________________1_________2_________3
		// __________________01234567890123456789012345678901234
		final String text = "Dann kann Anna es Ihrem Kollegen geben.";

		VFormUtil.Match match = VFormUtil.find(text, 0);

		assertEquals(18, match.startOffset);
		assertEquals(23, match.endOffset);

	}

	@Test
	public void testNoMatch1() {

		final String text = "Dann kann Anna es <brl:v-form>Ihrem</brl:v-form> Kollegen geben.";

		VFormUtil.Match match = VFormUtil.find(text, 0);

		assertEquals(VFormUtil.NULL_MATCH, match);

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

	/*
	 * 
	 *             +----------+----------+
	 *             |     =    |     !    |
	 *             | Positive | Negative |
	 * +-----------+----------+----------+
	 * | Ahead  ?  |    ?=    |    ?!    |
	 * +-----------+----------+----------+
	 * | Behind ?< |   ?<=    |   ?<!    |
	 * +-----------+----------+----------+
	 */

	// see java.util.regex.Pattern:2488: lookbehind must have fixed length
	@Test(expected = PatternSyntaxException.class)
	public void testPositiveLookBehindMustHaveFixedLength() {
		Pattern.compile("(?<=k\\s*)");
	}

	// see java.util.regex.Pattern:2488: lookbehind must have fixed length
	@Test(expected = PatternSyntaxException.class)
	public void testNegativeLookBehindMustHaveFixedLength() {
		Pattern.compile("(?<!k\\s*)");
	}

	@Test
	public void testRegexNegation() {

		final String regex = "(?<!bar)foo(?!bar)";
		final Pattern pattern = Pattern.compile(regex);

		assertFalse(pattern.matcher("barfoobar").find());
		assertFalse(pattern.matcher("barfoo").find());
		assertFalse(pattern.matcher("foobar").find());

		assertTrue(pattern.matcher("foo").find());
		assertTrue(pattern.matcher("bar foo bar").find());
	}

	@Test
	public void testRegexNegationvForm() {

		// final String regex =
		// "(?<!<\\s*brl\\s*:\\s*vform\\s*>\\s*)Sie(?!<\\s*/\\s*brl\\s*:\\s*vform\\s*>)";
		final String regex = "(?<!<brl:v-form>)Sie(?!</brl:v-form>)";
		final Pattern pattern = Pattern.compile(regex);
		assertFalse(pattern.matcher(
				"Das können <brl:v-form>Sie</brl:v-form> nicht.").find());
		assertFalse(pattern.matcher(
				"Das können <brl:v-form> Sie</brl:v-form> nicht.").find());
		assertFalse(pattern.matcher(
				"Das können <brl:v-form>Sie </brl:v-form> nicht.").find());

		assertTrue(pattern.matcher(
				"Das können <brl:v-form> Sie </brl:v-form> nicht.").find());

		assertTrue(pattern.matcher(
				"Das können <brl:v-form >Sie </brl:v-form> nicht.").find());

	}

	@Test
	public void testRegexNegationvFormWithSpaces() {

		// final String regex =
		// "(?<!<\\s*brl\\s*:\\s*vform\\s*>\\s*)Sie(?!<\\s*/\\s*brl\\s*:\\s*vform\\s*>)";
		final String regex = "(?<!<brl:v-form>)Sie(?!<\\s*/\\s*brl\\s*:\\s*v-form\\s*>)";
		final Pattern pattern = Pattern.compile(regex);
		assertFalse(pattern.matcher(
				"Das können <   brl:v-form>Sie</brl:v-form> nicht.").find());
		assertFalse(pattern.matcher(
				"Das können <brl:v-form> Sie</brl:v-form> nicht.").find());
		assertFalse(pattern.matcher(
				"Das können <brl:v-form>Sie </brl:v-form> nicht.").find());

		assertTrue(pattern.matcher(
				"Das können <brl:v-form> Sie </brl:v-form> nicht.").find());

		assertTrue(pattern.matcher(
				"Das können <brl:v-form >Sie </brl:v-form> nicht.").find());

	}

	@Test
	public void testNegativeLookahead() { // (?! ___ )
		// This regex asks:
		// Is there "John, Blank, a word that's not Smith, a capital letter
		// and a collection of word chars?
		final String regex = "John (?!Smith)[A-Z]\\w+";
		final Pattern pattern = Pattern.compile(regex);

		assertFalse(pattern.matcher("John Smith").find());
		assertTrue(pattern.matcher("John Jackson").find());
		assertTrue(pattern.matcher("John Westling").find());
		assertTrue(pattern.matcher("John Holmes").find());

	}

	@Test
	public void testPositiveLookAhead() { // (?= ___ )
		// This regex asks:
		// Is there a string that starts with 255 ?
		final String regex = "(?=255).*";
		final Pattern pattern = Pattern.compile(regex);

		assertTrue(pattern.matcher("255.0.0.1").find());
		assertTrue(pattern.matcher(" 255.0.0.1").find());
		assertTrue(pattern.matcher("255").find());
		assertTrue(pattern.matcher(" 255").find());
		assertFalse(pattern.matcher("25").find());
		assertEquals(
				"http://<ho>255.0.0.1. There, </ho>",
				pattern.matcher("http://255.0.0.1. There, ").replaceAll(
						"<ho>$0</ho>"));
		assertEquals("<ho>255.0.0.1. There, </ho>",
				pattern.matcher("255.0.0.1. There, ").replaceAll("<ho>$0</ho>"));
	}

	@Test
	public void testPositiveLookAheadWithAnchor() { // (?= ___ )
		// This regex asks:
		// Is there a string that starts with 255 ?
		final String regex = "(?=^255).*";
		final Pattern pattern = Pattern.compile(regex);

		assertTrue(pattern.matcher("255.0.0.1").find());
		assertFalse(pattern.matcher(" 255.0.0.1").find());
		assertTrue(pattern.matcher("255").find());
		assertFalse(pattern.matcher(" 255").find());
		assertFalse(pattern.matcher("25").find());
		assertEquals(
				"http://255.0.0.1. There, ",
				pattern.matcher("http://255.0.0.1. There, ").replaceAll(
						"<ho>$0</ho>"));
		assertEquals("<ho>255.0.0.1. There, </ho>",
				pattern.matcher("255.0.0.1. There, ").replaceAll("<ho>$0</ho>"));
	}

	@Test
	public void testPositiveLookBehind() { // (?<= ___ )
		// This regex asks:
		// Is there a non white space sequence preceded by http:// ?
		final String regex = "(?<=http://)\\S+";
		final Pattern pattern = Pattern.compile(regex);

		assertFalse(pattern.matcher("The Java2s website is found at ").find());
		assertTrue(pattern.matcher("http://www.java2s.com. There, ").find());
		assertFalse(pattern.matcher("you can find some Java examples.").find());
		assertEquals(
				"http://<ho>www.java2s.com.</ho> There, ",
				pattern.matcher("http://www.java2s.com. There, ").replaceAll(
						"<ho>$0</ho>"));
	}

	@Test
	public void testNegativeLookBehind() { // (?<! ___ )
		// http://www.lexemetech.com/2007/10/zero-width-negative-lookbehind.html
		// This regex asks:
		// Is there a number not preceded by + or - ?
		final String regex = "(?<![-+\\d])(\\d+)";
		final Pattern pattern = Pattern.compile(regex);

		assertFalse(pattern.matcher("+152 -444").find());
		assertTrue(pattern.matcher("+152 423 -444 -3 3").find());
		assertEquals("+152 <ho>423</ho> -444", pattern.matcher("+152 423 -444")
				.replaceAll("<ho>$1</ho>"));
		assertEquals("+152 +423 -444", pattern.matcher("+152 423 -444")
				.replaceAll("+$1"));
	}

	@Test
	public void testPhraseEndPW() {

	}

	@Test
	public void testPhraseEndPWQ() {

	}

	@Test
	public void testPhraseEndPQW() {

	}

	@Test
	public void testPhraseEndPQWQ() {

	}

	@Test
	public void testPhraseEndPCW() {

	}
}
